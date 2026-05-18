package br.com.suauniversidade.common;

import java.io.Serializable;

/**
 * Referencia a um objeto remoto residente no servidor.
 *
 * <p>E' a abstracao usada para <b>passagem por referencia</b> de objetos
 * remotos (Cursos, Departamentos). O cliente nao guarda a instancia: guarda
 * apenas este descritor, que o servidor resolve em seu repositorio
 * ({@link br.com.suauniversidade.server.RepositorioObjetosRemotos}).</p>
 *
 * <p>Conforme o enunciado, o campo principal e' uma String que representa
 * o nome do objeto que fornece o servico.</p>
 */
public class RemoteObjectRef implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Classe do objeto remoto (ex.: "Curso", "Departamento"). */
    private String classe;
    /** Identificador unico daquela instancia (chave no repositorio). */
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

    /** Chave canonica usada no repositorio do servidor. */
    public String chave() {
        return classe + "::" + id;
    }

    @Override
    public String toString() {
        return "RemoteObjectRef{" + chave() + "}";
    }
}
