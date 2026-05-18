package br.com.suauniversidade.server;

import br.com.suauniversidade.common.InvocadorRemoto;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implementacao do stub RMI. Recebe os bytes brutos da requisicao e
 * delega ao {@link DespachanteServico}, que e' quem executa o protocolo
 * requisicao-resposta.
 *
 * <p>Como herda de {@link UnicastRemoteObject}, esta classe ja vem com
 * seu proprio mecanismo de exportacao para o RMI runtime. Nao criamos
 * nenhum socket manualmente.</p>
 */
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
            // O RMI runtime expoe o host do cliente para invocacoes em curso.
            clientHost = InetAddress.getByName(RemoteServer.getClientHost());
        } catch (ServerNotActiveException | java.net.UnknownHostException ignored) {
            // Em testes locais ou ambientes sem hostname, seguimos sem isso.
        }
        return despachante.despachar(requisicaoBytes, clientHost, clientPort);
    }
}
