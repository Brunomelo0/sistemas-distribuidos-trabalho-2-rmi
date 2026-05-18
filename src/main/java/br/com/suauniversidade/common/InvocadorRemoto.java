package br.com.suauniversidade.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Stub RMI sobre o qual o protocolo requisicao-resposta e' implementado.
 *
 * <p>Note que <b>nao</b> existem metodos especificos do dominio nessa
 * interface: o cliente sempre empacota uma {@link Mensagem} em bytes e
 * chama {@link #receberRequisicao(byte[])}. Esse desenho deixa o despacho
 * por {@code methodId} no servidor, exatamente como descreve a secao 5.2
 * do Coulouris.</p>
 *
 * <p>Java RMI cuida do transporte real (nao criamos sockets); a logica de
 * pacote/desempacote do protocolo e' responsabilidade nossa.</p>
 */
public interface InvocadorRemoto extends Remote {

    /**
     * Recebe uma mensagem de requisicao empacotada e devolve a mensagem
     * de resposta empacotada.
     *
     * @param requisicaoBytes bytes da {@link Mensagem} de requisicao
     * @return bytes da {@link Mensagem} de resposta
     */
    byte[] receberRequisicao(byte[] requisicaoBytes) throws RemoteException;
}
