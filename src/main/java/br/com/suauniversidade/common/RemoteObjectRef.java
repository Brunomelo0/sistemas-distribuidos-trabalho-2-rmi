package br.com.suauniversidade.common;

import java.io.Serializable;

public class RemoteObjectRef implements Serializable {
    private static final long serialVersionUID = 1L;

    private String classe;
    private String id;

    public RemoteObjectRef() {}

    public RemoteObjectRef(String classe, String id) {
        this.classe = classe;
        this.id = id;
    }

    public String getClasse() { return classe; }
    public void setClasse(String classe) { this.classe = classe; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String chave() {
        return classe + "::" + id;
    }

    @Override
    public String toString() {
        return "RemoteObjectRef{" + chave() + "}";
    }
}
