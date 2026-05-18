package br.com.suauniversidade.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import br.com.suauniversidade.model.Aluno;
import br.com.suauniversidade.model.AlunoGraduacao;
import br.com.suauniversidade.model.AlunoIC;
import br.com.suauniversidade.model.AlunoPosGraduacao;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * Empacotamento / desempacotamento dos valores trocados entre cliente e
 * servidor em <b>representacao externa de dados</b>, usando JSON
 * (biblioteca Gson) sobre UTF-8.
 *
 * <p>Cobre dois niveis:</p>
 * <ol>
 *   <li>Empacotar/desempacotar a {@link Mensagem} inteira (header + payload);</li>
 *   <li>Empacotar/desempacotar valores arbitrarios (alunos, listas, etc.)
 *       para preencher o campo {@code arguments} da mensagem.</li>
 * </ol>
 *
 * <p>Inclui um {@link JsonDeserializer} customizado para reconstruir a
 * subclasse correta de {@link Aluno} (passagem por valor de tipo
 * polimorfico).</p>
 */
public final class Marshaller {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Aluno.class, new AlunoDeserializer())
            .create();

    private Marshaller() {}

    // --------- API generica ---------

    /** Empacota qualquer objeto em bytes UTF-8 contendo JSON. */
    public static byte[] empacotar(Object valor) {
        if (valor == null) return new byte[0];
        return GSON.toJson(valor).getBytes(StandardCharsets.UTF_8);
    }

    /** Desempacota bytes UTF-8 contendo JSON em um objeto do tipo informado. */
    public static <T> T desempacotar(byte[] bytes, Class<T> tipo) {
        if (bytes == null || bytes.length == 0) return null;
        String json = new String(bytes, StandardCharsets.UTF_8);
        return GSON.fromJson(json, tipo);
    }

    /** Desempacota usando um Type completo (uteis para listas genericas). */
    public static <T> T desempacotar(byte[] bytes, Type tipo) {
        if (bytes == null || bytes.length == 0) return null;
        String json = new String(bytes, StandardCharsets.UTF_8);
        return GSON.fromJson(json, tipo);
    }

    // --------- Mensagem (header do protocolo) ---------

    public static byte[] empacotarMensagem(Mensagem m) {
        return empacotar(m);
    }

    public static Mensagem desempacotarMensagem(byte[] bytes) {
        return desempacotar(bytes, Mensagem.class);
    }

    // --------- Suporte a polimorfismo de Aluno ---------

    /**
     * Deserializador que escolhe a subclasse correta de {@link Aluno}
     * com base no campo "tipo".
     */
    private static class AlunoDeserializer implements JsonDeserializer<Aluno> {
        @Override
        public Aluno deserialize(JsonElement json, Type typeOfT,
                                 JsonDeserializationContext context) {
            JsonObject obj = json.getAsJsonObject();
            String tipo = obj.has("tipo") ? obj.get("tipo").getAsString() : "Aluno";
            switch (tipo) {
                case "AlunoIC":           return context.deserialize(json, AlunoIC.class);
                case "AlunoPosGraduacao": return context.deserialize(json, AlunoPosGraduacao.class);
                case "AlunoGraduacao":    return context.deserialize(json, AlunoGraduacao.class);
                default:                  return context.deserialize(json, Aluno.class);
            }
        }
    }
}
