package br.com.suauniversidade.common;

import java.io.Serializable;

public class Mensagem implements Serializable {
    private static final long serialVersionUID = 1L;

    private TipoMensagem messageType;
    private int requestId;
    private String objectReference;
    private String methodId;
    private byte[] arguments;

    private boolean sucesso = true;
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
