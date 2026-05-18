package br.com.suauniversidade.model;

/**
 * Aluno de pos-graduacao (mestrado ou doutorado) com bolsa.
 *
 * <p><b>Composicao por extensao (e-um):</b> AlunoPosGraduacao <i>e-um</i> Aluno.
 * Tambem implementa o contrato {@link Remuneravel}.</p>
 */
public class AlunoPosGraduacao extends Aluno implements Remuneravel {
    private static final long serialVersionUID = 1L;

    private int diasTrabalhados;
    private double valorBolsa;

    public AlunoPosGraduacao() {
        super();
        this.tipo = "AlunoPosGraduacao";
    }

    public AlunoPosGraduacao(int id, String nome, String matricula) {
        super(id, nome, matricula);
        this.tipo = "AlunoPosGraduacao";
    }

    @Override public int getDiasTrabalhados() { return diasTrabalhados; }
    @Override public void setDiasTrabalhados(int dias) { this.diasTrabalhados = dias; }
    @Override public double getValorBolsa() { return valorBolsa; }
    @Override public void setValorBolsa(double valor) { this.valorBolsa = valor; }

    @Override
    public double calcularPagamento() {
        // Pos-graduandos recebem 10% a mais sobre o valor diario.
        return diasTrabalhados * (valorBolsa / 30.0) * 1.10;
    }
}
