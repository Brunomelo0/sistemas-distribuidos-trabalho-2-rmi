package br.com.suauniversidade.client;

import br.com.suauniversidade.common.RemoteObjectRef;
import br.com.suauniversidade.model.Aluno;
import br.com.suauniversidade.model.AlunoGraduacao;
import br.com.suauniversidade.model.AlunoIC;
import br.com.suauniversidade.model.AlunoPosGraduacao;

import java.util.List;

public class ClienteRMI {

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int porta = args.length > 1 ? Integer.parseInt(args[1]) : 1099;

        try {
            ProxyServico proxy = new ProxyServico(host, porta);
            System.out.println("[cliente] conectado em " + host + ":" + porta);
            System.out.println();

            cabecalho("1) criarDepartamento(\"DComp - Computacao\")");
            RemoteObjectRef refDComp = proxy.criarDepartamento("DComp - Computacao");
            System.out.println("    -> retorno: " + refDComp);

            cabecalho("2) criarCurso(\"Ciencia da Computacao\", refDComp)");
            RemoteObjectRef refCC = proxy.criarCurso("Ciencia da Computacao", refDComp);
            System.out.println("    -> retorno: " + refCC);

            cabecalho("3) criarCurso(\"Sistemas de Informacao\", refDComp)");
            RemoteObjectRef refSI = proxy.criarCurso("Sistemas de Informacao", refDComp);
            System.out.println("    -> retorno: " + refSI);

            cabecalho("4) matricularAluno(AlunoGraduacao Ana, refCC)");
            Aluno ana = new AlunoGraduacao(1, "Ana Silva", "2024001");
            System.out.println("    aluno (cliente): " + ana);
            proxy.matricularAluno(ana, refCC);

            cabecalho("5) matricularAluno(AlunoIC Bruno, refCC)");
            AlunoIC bruno = new AlunoIC(2, "Bruno Costa", "2024002");
            bruno.setDiasTrabalhados(20);
            bruno.setValorBolsa(700.0);
            System.out.println("    aluno (cliente): " + bruno);
            proxy.matricularAluno(bruno, refCC);

            cabecalho("6) matricularAluno(AlunoPosGraduacao Carla, refSI)");
            AlunoPosGraduacao carla = new AlunoPosGraduacao(3, "Carla Lima", "2024003");
            carla.setDiasTrabalhados(30);
            carla.setValorBolsa(2200.0);
            System.out.println("    aluna (cliente): " + carla);
            proxy.matricularAluno(carla, refSI);

            cabecalho("7) emitirFolhaPagamento(Bruno)");
            double folhaBruno = proxy.emitirFolhaPagamento(bruno);
            System.out.printf("    -> retorno: R$ %.2f%n", folhaBruno);

            cabecalho("8) emitirFolhaPagamento(Carla)");
            double folhaCarla = proxy.emitirFolhaPagamento(carla);
            System.out.printf("    -> retorno: R$ %.2f%n", folhaCarla);

            cabecalho("9) listarAlunosCurso(refCC)");
            List<Aluno> alunosCC = proxy.listarAlunosCurso(refCC);
            for (Aluno a : alunosCC) {
                System.out.println("    - " + a);
            }

            cabecalho("10) listarCursosDepartamento(refDComp)");
            List<String> cursos = proxy.listarCursosDepartamento(refDComp);
            for (String c : cursos) {
                System.out.println("    - " + c);
            }

            cabecalho("11) emitirFolhaPagamento(Ana) -- caso de erro esperado");
            try {
                proxy.emitirFolhaPagamento(ana);
                System.out.println("    !! deveria ter falhado");
            } catch (RuntimeException e) {
                System.out.println("    -> erro recebido do servidor: " + e.getMessage());
            }

            System.out.println();
            System.out.println("[cliente] Cenarios concluidos.");
        } catch (Exception e) {
            System.err.println("[cliente] Falha:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void cabecalho(String titulo) {
        System.out.println();
        System.out.println("=== " + titulo + " ===");
    }
}
