package br.com.suauniversidade.common;

/**
 * Tipo da mensagem trafegada no protocolo requisicao-resposta
 * (Coulouris, secao 5.2).
 */
public enum TipoMensagem {
    /** Requisicao do cliente para o servidor. */
    REQUEST,
    /** Resposta do servidor para o cliente. */
    REPLY
}
