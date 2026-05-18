package br.com.suauniversidade.model;

import java.io.Serializable;

/**
 * Entidade base do dominio. Toda matricula, IC ou pos-graduacao "e-um" Aluno.
 *
 * <p>Os campos sao serializaveis via JSON (representacao externa de dados),
 * de modo que instancias podem trafegar do cliente para o servidor por
 * <b>passagem de valor</b>.</p>
 */
public class Aluno implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Discriminador usado na (de)serializacao JSON para reconstruir a subclasse correta. */
    protected String tipo;

    protected int id;
    protected String nome;
    protected String matricula;

    public Aluno() {
        this.tipo = this.getClass().getSimpleName();
    }

    public Aluno(int id, String nome, String matricula) {
        this();
        this.id = id;
        this.nome = nome;
        this.matricula = matricula;
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    @Override
    public String toString() {
        return String.format("%s[id=%d, nome=%s, matricula=%s]",
                tipo, id, nome, matricula);
    }
}
