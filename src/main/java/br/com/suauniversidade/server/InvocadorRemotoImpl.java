package br.com.suauniversidade.server;

import br.com.suauniversidade.common.InvocadorRemoto;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;


public class InvocadorRemotoImpl extends UnicastRemoteObject implements InvocadorRemoto {
    private static final long serialVersionUID = 1L;

    private final DespachanteServico despachante;

    public InvocadorRemotoImpl(DespachanteServico despachante) throws RemoteException {
        super();
        this.despachante = despachante;
    }

    @Override
    public byte[] receberRequisicao(byte[] requisicaoBytes) throws RemoteException {
        InetAddress clientHost = null;
        int clientPort = 0;
        try {
            clientHost = InetAddress.getByName(RemoteServer.getClientHost());
        } catch (ServerNotActiveException | java.net.UnknownHostException ignored) {
        }
        return despachante.despachar(requisicaoBytes, clientHost, clientPort);
    }
}
