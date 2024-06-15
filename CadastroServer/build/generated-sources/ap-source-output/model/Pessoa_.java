package model;

import javax.annotation.processing.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.Movimento;
import model.PessoaFisica;
import model.PessoaJuridica;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2024-06-14T20:52:15", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Pessoa.class)
public class Pessoa_ { 

    public static volatile SingularAttribute<Pessoa, Character> tipo;
    public static volatile SingularAttribute<Pessoa, String> cidade;
    public static volatile SingularAttribute<Pessoa, String> estado;
    public static volatile SingularAttribute<Pessoa, String> telefone;
    public static volatile CollectionAttribute<Pessoa, PessoaFisica> pessoaFisicaCollection;
    public static volatile SingularAttribute<Pessoa, String> logradouro;
    public static volatile SingularAttribute<Pessoa, String> nome;
    public static volatile SingularAttribute<Pessoa, Integer> pessoaID;
    public static volatile CollectionAttribute<Pessoa, Movimento> movimentoCollection;
    public static volatile CollectionAttribute<Pessoa, PessoaJuridica> pessoaJuridicaCollection;
    public static volatile SingularAttribute<Pessoa, String> email;

}