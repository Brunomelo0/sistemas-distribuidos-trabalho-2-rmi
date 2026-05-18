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

/**
 * Proxy do cliente. Esconde a complexidade do protocolo requisicao-resposta:
 * cada metodo publico desta classe empacota os argumentos, chama
 * {@link #doOperation(RemoteObjectRef, String, byte[])} e desempacota o
 * retorno.
 *
 * <p>Conforme o livro texto (Coulouris, secao 5.2), o nucleo da
 * implementacao do cliente e' o metodo {@code doOperation}, que troca uma
 * requisicao por uma resposta atraves do objeto remoto.</p>
 */
public class ProxyServico {

    /** Referencia conceitual ao servico no servidor (campo objectReference). */
    private static final String NOME_SERVICO = "ServicoControleAlunos";

    private final InvocadorRemoto invocador;
    private final AtomicInteger contadorRequest = new AtomicInteger(0);

    public ProxyServico(String host, int porta) throws Exception {
        Registry registry = LocateRegistry.getRegistry(host, porta);
        this.invocador = (InvocadorRemoto) registry.lookup("InvocadorRemoto");
    }

    // =========================================================================
    // doOperation - nucleo do protocolo requisicao-resposta (Coulouris 5.2)
    // =========================================================================

    /**
     * Envia uma mensagem de requisicao para o objeto remoto e devolve a
     * resposta.
     *
     * <p>A assinatura difere ligeiramente da sugerida pelo autor (que usa
     * {@code int methodId}): aqui {@code methodId} e' uma String,
     * conforme permitido pelo enunciado e mais natural para um dispatcher
     * por nome.</p>
     *
     * @param o         referencia conceitual ao objeto remoto que oferece o servico
     * @param methodId  nome do metodo a ser invocado
     * @param arguments argumentos ja empacotados em representacao externa
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

    // =========================================================================
    // API tipada que o cliente final usa. Cada metodo:
    //  1. monta um Map<String,Object> com os argumentos nomeados;
    //  2. empacota em JSON;
    //  3. chama doOperation;
    //  4. desempacota o retorno.
    // =========================================================================

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
