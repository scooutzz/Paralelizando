package model;

import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.Pessoa;
import model.Produto;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2024-06-10T22:13:53", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Movimento.class)
public class Movimento_ { 

    public static volatile SingularAttribute<Movimento, BigDecimal> precoUnitario;
    public static volatile SingularAttribute<Movimento, Produto> produtoID;
    public static volatile SingularAttribute<Movimento, Character> tipo;
    public static volatile SingularAttribute<Movimento, Integer> movimentoID;
    public static volatile SingularAttribute<Movimento, Pessoa> pessoaID;
    public static volatile SingularAttribute<Movimento, Integer> usuarioID;
    public static volatile SingularAttribute<Movimento, Integer> quantidade;

}