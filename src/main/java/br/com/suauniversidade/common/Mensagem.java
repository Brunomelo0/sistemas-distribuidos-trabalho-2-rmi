package br.com.suauniversidade.common;

import java.io.Serializable;

/**
 * Mensagem do protocolo requisicao-resposta (Coulouris, secao 5.2).
 *
 * <p>Sao os mesmos campos da figura citada no enunciado:</p>
 * <ul>
 *   <li><b>messageType</b>: indica se e' REQUEST ou REPLY;</li>
 *   <li><b>requestId</b>: identifica unicamente a requisicao para casar com a resposta;</li>
 *   <li><b>objectReference</b>: nome do objeto que fornece o servico (String);</li>
 *   <li><b>methodId</b>: nome do metodo a ser invocado (String);</li>
 *   <li><b>arguments</b>: argumentos empacotados em representacao externa
 *       (aqui, JSON), trafegando como bytes.</li>
 * </ul>
 *
 * <p>Em respostas, o campo {@code arguments} carrega o valor de retorno
 * (tambem em JSON). Os campos {@code sucesso} e {@code erro} permitem que o
 * servidor sinalize falhas durante o despacho.</p>
 */
public class Mensagem implements Serializable {
    private static final long serialVersionUID = 1L;

    private TipoMensagem messageType;
    private int requestId;
    private String objectReference;
    private String methodId;
    /** Argumentos em representacao externa (JSON em UTF-8). */
    private byte[] arguments;

    /** Indica sucesso no despacho (preenchido apenas em REPLY). */
    private boolean sucesso = true;
    /** Mensagem de erro (preenchida apenas em REPLY com falha). */
    private String erro;

    public Mensagem() {}

    public Mensagem(TipoMensagem messageType, int requestId,
                    String objectReference, String methodId,
                    byte[] arguments) {
        this.messageType = messageType;
        this.requestId = requestId;
        this.objectReference = objectReference;
        this.methodId = methodId;
        this.arguments = arguments;
    }

    public TipoMensagem getMessageType() { return messageType; }
    public void setMessageType(TipoMensagem messageType) { this.messageType = messageType; }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public String getObjectReference() { return objectReference; }
    public void setObjectReference(String objectReference) { this.objectReference = objectReference; }

    public String getMethodId() { return methodId; }
    public void setMethodId(String methodId) { this.methodId = methodId; }

    public byte[] getArguments() { return arguments; }
    public void setArguments(byte[] arguments) { this.arguments = arguments; }

    public boolean isSucesso() { return sucesso; }
    public void setSucesso(boolean sucesso) { this.sucesso = sucesso; }

    public String getErro() { return erro; }
    public void setErro(String erro) { this.erro = erro; }
}
