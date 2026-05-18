package br.com.suauniversidade.model;

/**
 * Aluno de graduacao.
 *
 * <p><b>Composicao por extensao (e-um):</b> AlunoGraduacao <i>e-um</i> Aluno.</p>
 */
public class AlunoGraduacao extends Aluno {
    private static final long serialVersionUID = 1L;

    public AlunoGraduacao() {
        super();
        this.tipo = "AlunoGraduacao";
    }

    public AlunoGraduacao(int id, String nome, String matricula) {
        super(id, nome, matricula);
        this.tipo = "AlunoGraduacao";
    }
}
