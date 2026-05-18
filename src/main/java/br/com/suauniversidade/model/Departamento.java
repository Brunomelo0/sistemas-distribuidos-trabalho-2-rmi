package br.com.suauniversidade.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Departamento academico.
 *
 * <p><b>Composicao por agregacao (tem-um):</b> Departamento <i>tem-uma</i>
 * lista de {@link Curso}.</p>
 *
 * <p>Departamento e' um <i>objeto remoto</i>: tambem fica residente no
 * servidor e e' manipulado pelo cliente por referencia.</p>
 */
public class Departamento implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private List<Curso> cursos = new ArrayList<>();

    public Departamento() {}

    public Departamento(String nome) {
        this.nome = nome;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public List<Curso> getCursos() { return cursos; }

    public void adicionarCurso(Curso curso) {
        this.cursos.add(curso);
    }
}
