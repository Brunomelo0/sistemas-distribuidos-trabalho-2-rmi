package br.com.suauniversidade.server;

import br.com.suauniversidade.common.Marshaller;
import br.com.suauniversidade.common.Mensagem;
import br.com.suauniversidade.common.RemoteObjectRef;
import br.com.suauniversidade.common.TipoMensagem;
import br.com.suauniversidade.model.Aluno;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

public class DespachanteServico {

    public static final String NOME_SERVICO = "ServicoControleAlunos";

    private final ServicoControleAlunos servico;

    public DespachanteServico(ServicoControleAlunos servico) {
        this.servico = servico;
    }

    /**
     * Desempacota a mensagem de requisicao recebida pela porta servidora.
     */
    public Mensagem getRequest(byte[] requisicaoBytes) {
        return Marshaller.desempacotarMensagem(requisicaoBytes);
    }

    /**
     * Empacota uma resposta para envio ao cliente. Sob RMI, host e porta
     * sao informativos: o transporte ja sabe a quem entregar.
     */
    public byte[] sendReply(byte[] reply, InetAddress clientHost, int clientPort) {
        System.out.printf("  [transporte] reply de %d bytes para %s:%d%n",
                reply.length,
                clientHost != null ? clientHost.getHostAddress() : "?",
                clientPort);
        return reply;
    }

    public byte[] despachar(byte[] requisicaoBytes, InetAddress clientHost, int clientPort) {
        Mensagem requisicao = getRequest(requisicaoBytes);
        Mensagem resposta = new Mensagem();
        resposta.setMessageType(TipoMensagem.REPLY);
        resposta.setRequestId(requisicao.getRequestId());
        resposta.setObjectReference(requisicao.getObjectReference());
        resposta.setMethodId(requisicao.getMethodId());

        try {
            if (!NOME_SERVICO.equals(requisicao.getObjectReference())) {
                throw new IllegalArgumentException(
                        "Objeto remoto desconhecido: " + requisicao.getObjectReference());
            }

            byte[] retorno = invocar(requisicao.getMethodId(), requisicao.getArguments());
            resposta.setArguments(retorno);
            resposta.setSucesso(true);
        } catch (Exception e) {
            resposta.setSucesso(false);
            resposta.setErro(e.getClass().getSimpleName() + ": " + e.getMessage());
            resposta.setArguments(new byte[0]);
            System.err.println("  [erro] " + resposta.getErro());
        }

        byte[] respostaBytes = Marshaller.empacotarMensagem(resposta);
        return sendReply(respostaBytes, clientHost, clientPort);
    }

    private byte[] invocar(String methodId, byte[] argsBytes) {
        switch (methodId) {

            case "criarDepartamento": {
                Map<String, Object> args = parseArgs(argsBytes);
                String nome = (String) args.get("nome");
                RemoteObjectRef ref = servico.criarDepartamento(nome);
                return Marshaller.empacotar(ref);
            }

            case "criarCurso": {
                Map<String, Object> args = parseArgs(argsBytes);
                String nomeCurso = (String) args.get("nomeCurso");
                RemoteObjectRef refDep = Marshaller.desempacotar(
                        Marshaller.empacotar(args.get("refDepartamento")),
                        RemoteObjectRef.class);
                RemoteObjectRef ref = servico.criarCurso(nomeCurso, refDep);
                return Marshaller.empacotar(ref);
            }

            case "matricularAluno": {
                Map<String, Object> args = parseArgs(argsBytes);
                Aluno aluno = Marshaller.desempacotar(
                        Marshaller.empacotar(args.get("aluno")), Aluno.class);
                RemoteObjectRef refCurso = Marshaller.desempacotar(
                        Marshaller.empacotar(args.get("refCurso")),
                        RemoteObjectRef.class);
                boolean ok = servico.matricularAluno(aluno, refCurso);
                return Marshaller.empacotar(ok);
            }

            case "emitirFolhaPagamento": {
                Map<String, Object> args = parseArgs(argsBytes);
                Aluno aluno = Marshaller.desempacotar(
                        Marshaller.empacotar(args.get("aluno")), Aluno.class);
                double valor = servico.emitirFolhaPagamento(aluno);
                return Marshaller.empacotar(valor);
            }

            case "listarAlunosCurso": {
                Map<String, Object> args = parseArgs(argsBytes);
                RemoteObjectRef refCurso = Marshaller.desempacotar(
                        Marshaller.empacotar(args.get("refCurso")),
                        RemoteObjectRef.class);
                List<Aluno> lista = servico.listarAlunosCurso(refCurso);
                return Marshaller.empacotar(lista);
            }

            case "listarCursosDepartamento": {
                Map<String, Object> args = parseArgs(argsBytes);
                RemoteObjectRef refDep = Marshaller.desempacotar(
                        Marshaller.empacotar(args.get("refDepartamento")),
                        RemoteObjectRef.class);
                List<String> nomes = servico.listarCursosDepartamento(refDep);
                return Marshaller.empacotar(nomes);
            }

            default:
                throw new IllegalArgumentException("Metodo desconhecido: " + methodId);
        }
    }

    private Map<String, Object> parseArgs(byte[] argsBytes) {
        Type tipo = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> args = Marshaller.desempacotar(argsBytes, tipo);
        return args != null ? args : Map.of();
    }
}
