package br.com.suauniversidade.server;

import br.com.suauniversidade.common.InvocadorRemoto;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServidorRMI {

    public static final int PORTA_REGISTRY = 1099;
    public static final String NOME_BINDING = "InvocadorRemoto";

    public static void main(String[] args) {
        try {
            RepositorioObjetosRemotos repositorio = new RepositorioObjetosRemotos();
            ServicoControleAlunos servico = new ServicoControleAlunos(repositorio);
            DespachanteServico despachante = new DespachanteServico(servico);
            InvocadorRemoto stub = new InvocadorRemotoImpl(despachante);

            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(PORTA_REGISTRY);
                System.out.println("[servidor] Registry RMI criado na porta " + PORTA_REGISTRY);
            } catch (Exception e) {
                registry = LocateRegistry.getRegistry(PORTA_REGISTRY);
                System.out.println("[servidor] Reaproveitando registry existente na porta "
                        + PORTA_REGISTRY);
            }

            registry.rebind(NOME_BINDING, stub);
            System.out.println("[servidor] InvocadorRemoto publicado como \""
                    + NOME_BINDING + "\".");
            System.out.println("[servidor] Pronto. Aguardando chamadas. (Ctrl+C para encerrar.)");
        } catch (Exception e) {
            System.err.println("[servidor] Falha ao inicializar:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
