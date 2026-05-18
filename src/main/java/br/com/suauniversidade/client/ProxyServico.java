package br.com.suauniversidade.client;

import br.com.suauniversidade.common.InvocadorRemoto;
import br.com.suauniversidade.common.Marshaller;
import br.com.suauniversidade.common.Mensagem;
import br.com.suauniversidade.common.RemoteObjectRef;
import br.com.suauniversidade.common.TipoMensagem;
import br.com.suauniversidade.model.Aluno;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class ProxyServico {

    private static final String NOME_SERVICO = "ServicoControleAlunos";

    private final InvocadorRemoto invocador;
    private final AtomicInteger contadorRequest = new AtomicInteger(0);

    public ProxyServico(String host, int porta) throws Exception {
        Registry registry = LocateRegistry.getRegistry(host, porta);
        this.invocador = (InvocadorRemoto) registry.lookup("InvocadorRemoto");
    }


    /**
     * Envia uma mensagem de requisicao para o objeto remoto e devolve a
     * resposta.
     *
     * @param o         referencia conceitual ao objeto remoto que oferece o servico
     * @param methodId  nome do metodo a ser invocado
     * @param arguments argumentos ja empacotados
     * @return bytes do retorno (campo {@code arguments} da resposta)
     */
    public byte[] doOperation(RemoteObjectRef o, String methodId, byte[] arguments)
            throws Exception {
        Mensagem requisicao = new Mensagem(
                TipoMensagem.REQUEST,
                contadorRequest.incrementAndGet(),
                o.getId(),
                methodId,
                arguments);

        byte[] reqBytes = Marshaller.empacotarMensagem(requisicao);
        byte[] respBytes = invocador.receberRequisicao(reqBytes);
        Mensagem resposta = Marshaller.desempacotarMensagem(respBytes);

        if (resposta == null) {
            throw new RuntimeException("Resposta vazia do servidor");
        }
        if (!resposta.isSucesso()) {
            throw new RuntimeException("Erro remoto: " + resposta.getErro());
        }
        if (resposta.getRequestId() != requisicao.getRequestId()) {
            throw new RuntimeException("requestId nao casa: esperado "
                    + requisicao.getRequestId() + ", recebido " + resposta.getRequestId());
        }
        return resposta.getArguments();
    }

    public RemoteObjectRef criarDepartamento(String nome) throws Exception {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("nome", nome);
        byte[] argsBytes = Marshaller.empacotar(args);
        byte[] retorno = doOperation(referenciaServico(), "criarDepartamento", argsBytes);
        return Marshaller.desempacotar(retorno, RemoteObjectRef.class);
    }

    public RemoteObjectRef criarCurso(String nomeCurso, RemoteObjectRef refDepartamento)
            throws Exception {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("nomeCurso", nomeCurso);
        args.put("refDepartamento", refDepartamento);
        byte[] argsBytes = Marshaller.empacotar(args);
        byte[] retorno = doOperation(referenciaServico(), "criarCurso", argsBytes);
        return Marshaller.desempacotar(retorno, RemoteObjectRef.class);
    }

    public boolean matricularAluno(Aluno aluno, RemoteObjectRef refCurso) throws Exception {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("aluno", aluno);
        args.put("refCurso", refCurso);
        byte[] argsBytes = Marshaller.empacotar(args);
        byte[] retorno = doOperation(referenciaServico(), "matricularAluno", argsBytes);
        return Marshaller.desempacotar(retorno, Boolean.class);
    }

    public double emitirFolhaPagamento(Aluno aluno) throws Exception {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("aluno", aluno);
        byte[] argsBytes = Marshaller.empacotar(args);
        byte[] retorno = doOperation(referenciaServico(), "emitirFolhaPagamento", argsBytes);
        return Marshaller.desempacotar(retorno, Double.class);
    }

    public List<Aluno> listarAlunosCurso(RemoteObjectRef refCurso) throws Exception {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("refCurso", refCurso);
        byte[] argsBytes = Marshaller.empacotar(args);
        byte[] retorno = doOperation(referenciaServico(), "listarAlunosCurso", argsBytes);
        Type tipo = new TypeToken<List<Aluno>>(){}.getType();
        return Marshaller.desempacotar(retorno, tipo);
    }

    public List<String> listarCursosDepartamento(RemoteObjectRef refDepartamento)
            throws Exception {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("refDepartamento", refDepartamento);
        byte[] argsBytes = Marshaller.empacotar(args);
        byte[] retorno = doOperation(referenciaServico(), "listarCursosDepartamento", argsBytes);
        Type tipo = new TypeToken<List<String>>(){}.getType();
        return Marshaller.desempacotar(retorno, tipo);
    }

    /**
     * Referencia "ficticia" usada no campo objectReference das mensagens.
     * Esta nao e' uma referencia a um objeto criado pelo cliente, mas sim
     * ao servico bem-conhecido que oferece os metodos remotos.
     */
    private RemoteObjectRef referenciaServico() {
        return new RemoteObjectRef("Servico", NOME_SERVICO);
    }
}
