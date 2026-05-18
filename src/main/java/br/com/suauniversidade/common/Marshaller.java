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

public final class Marshaller {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Aluno.class, new AlunoDeserializer())
            .create();

    private Marshaller() {}


    public static byte[] empacotar(Object valor) {
        if (valor == null) return new byte[0];
        return GSON.toJson(valor).getBytes(StandardCharsets.UTF_8);
    }

    public static <T> T desempacotar(byte[] bytes, Class<T> tipo) {
        if (bytes == null || bytes.length == 0) return null;
        String json = new String(bytes, StandardCharsets.UTF_8);
        return GSON.fromJson(json, tipo);
    }

    public static <T> T desempacotar(byte[] bytes, Type tipo) {
        if (bytes == null || bytes.length == 0) return null;
        String json = new String(bytes, StandardCharsets.UTF_8);
        return GSON.fromJson(json, tipo);
    }


    public static byte[] empacotarMensagem(Mensagem m) {
        return empacotar(m);
    }

    public static Mensagem desempacotarMensagem(byte[] bytes) {
        return desempacotar(bytes, Mensagem.class);
    }

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
