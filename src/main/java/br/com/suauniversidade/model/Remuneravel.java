package br.com.suauniversidade.model;

/**
 * Contrato para entidades que recebem remuneracao no servidor.
 * Implementado por {@link AlunoIC} e {@link AlunoPosGraduacao}.
 */
public interface Remuneravel {
    int getDiasTrabalhados();
    void setDiasTrabalhados(int dias);
    double getValorBolsa();
    void setValorBolsa(double valor);
    double calcularPagamento();
}
