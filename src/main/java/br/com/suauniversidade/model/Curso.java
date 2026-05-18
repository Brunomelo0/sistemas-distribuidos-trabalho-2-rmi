package br.com.suauniversidade.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Curso oferecido pela universidade.
 *
 * <p><b>Composicao por agregacao (tem-um):</b> Curso <i>tem-uma</i> lista de
 * {@link Aluno}.</p>
 *
 * <p>Curso e' um <i>objeto remoto</i> mantido pelo servidor: o cliente nao
 * recebe sua instancia, apenas a sua referencia
 * ({@link br.com.suauniversidade.common.RemoteObjectRef}).</p>
 */
public class Curso implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nomeCurso;
    private List<Aluno> alunos = new ArrayList<>();

    public Curso() {}

    public Curso(String nomeCurso) {
        this.nomeCurso = nomeCurso;
    }

    public String getNomeCurso() { return nomeCurso; }
    public void setNomeCurso(String nomeCurso) { this.nomeCurso = nomeCurso; }

    public List<Aluno> getAlunos() { return alunos; }

    public void matricular(Aluno aluno) {
        this.alunos.add(aluno);
    }

    public int totalMatriculados() {
        return alunos.size();
    }
}
