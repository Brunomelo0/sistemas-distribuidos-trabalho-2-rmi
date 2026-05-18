package br.com.suauniversidade.model;

/**
 * Aluno bolsista de Iniciacao Cientifica.
 *
 * <p><b>Composicao por extensao (e-um):</b> AlunoIC <i>e-um</i> AlunoGraduacao
 * (que por sua vez e-um Aluno). Tambem implementa o contrato
 * {@link Remuneravel}.</p>
 */
public class AlunoIC extends AlunoGraduacao implements Remuneravel {
    private static final long serialVersionUID = 1L;

    private int diasTrabalhados;
    private double valorBolsa;

    public AlunoIC() {
        super();
        this.tipo = "AlunoIC";
    }

    public AlunoIC(int id, String nome, String matricula) {
        super(id, nome, matricula);
        this.tipo = "AlunoIC";
    }

    @Override public int getDiasTrabalhados() { return diasTrabalhados; }
    @Override public void setDiasTrabalhados(int dias) { this.diasTrabalhados = dias; }
    @Override public double getValorBolsa() { return valorBolsa; }
    @Override public void setValorBolsa(double valor) { this.valorBolsa = valor; }

    @Override
    public double calcularPagamento() {
        return diasTrabalhados * (valorBolsa / 30.0);
    }
}
