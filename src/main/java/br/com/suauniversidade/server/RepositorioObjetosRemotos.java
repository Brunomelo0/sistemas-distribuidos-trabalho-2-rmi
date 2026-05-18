package br.com.suauniversidade.server;

import br.com.suauniversidade.common.RemoteObjectRef;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RepositorioObjetosRemotos {

    private final Map<String, Object> objetos = new ConcurrentHashMap<>();
    private final AtomicInteger contador = new AtomicInteger(0);

    /** Registra um novo objeto e retorna a referencia que o representa. */
    public RemoteObjectRef registrar(String classe, Object objeto) {
        String id = classe.toLowerCase() + "-" + contador.incrementAndGet();
        RemoteObjectRef ref = new RemoteObjectRef(classe, id);
        objetos.put(ref.chave(), objeto);
        return ref;
    }

    /** Resolve a referencia, devolvendo o objeto remoto guardado. */
    @SuppressWarnings("unchecked")
    public <T> T resolver(RemoteObjectRef ref, Class<T> tipo) {
        Object obj = objetos.get(ref.chave());
        if (obj == null) {
            throw new IllegalArgumentException(
                    "Objeto remoto nao encontrado: " + ref.chave());
        }
        if (!tipo.isInstance(obj)) {
            throw new ClassCastException(
                    "Referencia " + ref.chave() + " nao e' um " + tipo.getSimpleName());
        }
        return (T) obj;
    }

    public boolean contem(RemoteObjectRef ref) {
        return objetos.containsKey(ref.chave());
    }
}
