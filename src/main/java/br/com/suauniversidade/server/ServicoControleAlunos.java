package br.com.suauniversidade.server;

import br.com.suauniversidade.common.RemoteObjectRef;
import br.com.suauniversidade.model.Aluno;
import br.com.suauniversidade.model.Curso;
import br.com.suauniversidade.model.Departamento;
import br.com.suauniversidade.model.Remuneravel;

import java.util.List;

/**
 * Logica de negocio invocada pelo despachante.
 *
 * <p>Esta classe NAO conhece o protocolo: opera apenas em termos do
 * dominio. O dispatcher e' quem traduz {@code Mensagem -> chamada de
 * metodo}.</p>
 */
public class ServicoControleAlunos {

    private final RepositorioObjetosRemotos repositorio;

    public ServicoControleAlunos(RepositorioObjetosRemotos repositorio) {
        this.repositorio = repositorio;
    }

    /** Metodo remoto: cria um Departamento no servidor e devolve sua referencia. */
    public RemoteObjectRef criarDepartamento(String nome) {
        Departamento dep = new Departamento(nome);
        RemoteObjectRef ref = repositorio.registrar("Departamento", dep);
        System.out.printf("  [servico] Departamento criado: %s (%s)%n", nome, ref.chave());
        return ref;
    }

    /** Metodo remoto: cria um Curso e o associa a um Departamento existente. */
    public RemoteObjectRef criarCurso(String nomeCurso, RemoteObjectRef refDepartamento) {
        Departamento dep = repositorio.resolver(refDepartamento, Departamento.class);
        Curso curso = new Curso(nomeCurso);
        dep.adicionarCurso(curso);
        RemoteObjectRef ref = repositorio.registrar("Curso", curso);
        System.out.printf("  [servico] Curso criado: %s (%s) no departamento %s%n",
                nomeCurso, ref.chave(), dep.getNome());
        return ref;
    }

    /** Metodo remoto: matricula o aluno (passado por valor) no curso (passado por referencia). */
    public boolean matricularAluno(Aluno aluno, RemoteObjectRef refCurso) {
        Curso curso = repositorio.resolver(refCurso, Curso.class);
        curso.matricular(aluno);
        System.out.printf("  [servico] %s matriculado em %s (total: %d)%n",
                aluno.getNome(), curso.getNomeCurso(), curso.totalMatriculados());
        return true;
    }

    /** Metodo remoto: calcula o pagamento de um aluno bolsista (passado por valor). */
    public double emitirFolhaPagamento(Aluno aluno) {
        if (!(aluno instanceof Remuneravel)) {
            throw new IllegalArgumentException(
                    "Aluno do tipo " + aluno.getTipo() + " nao e' Remuneravel.");
        }
        Remuneravel r = (Remuneravel) aluno;
        double pagamento = r.calcularPagamento();
        System.out.printf("  [servico] Folha de %s: R$ %.2f (%d dias, bolsa R$ %.2f)%n",
                aluno.getNome(), pagamento, r.getDiasTrabalhados(), r.getValorBolsa());
        return pagamento;
    }

    /** Metodo remoto: lista alunos de um curso (Curso por referencia, lista por valor). */
    public List<Aluno> listarAlunosCurso(RemoteObjectRef refCurso) {
        Curso curso = repositorio.resolver(refCurso, Curso.class);
        System.out.printf("  [servico] Listando %d alunos do curso %s%n",
                curso.totalMatriculados(), curso.getNomeCurso());
        return curso.getAlunos();
    }

    /** Metodo remoto: lista os nomes dos cursos de um departamento. */
    public List<String> listarCursosDepartamento(RemoteObjectRef refDepartamento) {
        Departamento dep = repositorio.resolver(refDepartamento, Departamento.class);
        List<String> nomes = dep.getCursos().stream()
                .map(Curso::getNomeCurso)
                .toList();
        System.out.printf("  [servico] Departamento %s tem %d cursos%n",
                dep.getNome(), nomes.size());
        return nomes;
    }
}
