package br.com.suauniversidade.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

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
