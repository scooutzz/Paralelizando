package model;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "Movimento")
@NamedQueries({
    @NamedQuery(name = "Movimento.findAll", query = "SELECT m FROM Movimento m"),
    @NamedQuery(name = "Movimento.findByMovimentoID", query = "SELECT m FROM Movimento m WHERE m.movimentoID = :movimentoID"),
    @NamedQuery(name = "Movimento.findByUsuarioID", query = "SELECT m FROM Movimento m WHERE m.usuarioID = :usuarioID"),
    @NamedQuery(name = "Movimento.findByTipo", query = "SELECT m FROM Movimento m WHERE m.tipo = :tipo"),
    @NamedQuery(name = "Movimento.findByQuantidade", query = "SELECT m FROM Movimento m WHERE m.quantidade = :quantidade"),
    @NamedQuery(name = "Movimento.findByPrecoUnitario", query = "SELECT m FROM Movimento m WHERE m.precoUnitario = :precoUnitario")})
public class Movimento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "MovimentoID")
    private Integer movimentoID;
    @Column(name = "UsuarioID")
    private Integer usuarioID;
    @Column(name = "Tipo")
    private Character tipo;
    @Column(name = "Quantidade")
    private Integer quantidade;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "PrecoUnitario")
    private BigDecimal precoUnitario;
    @JoinColumn(name = "PessoaID", referencedColumnName = "PessoaID")
    @ManyToOne
    private Pessoa pessoaID;
    @JoinColumn(name = "ProdutoID", referencedColumnName = "ProdutoID")
    @ManyToOne
    private Produto produtoID;

    public Movimento() {
    }

    public Movimento(Integer movimentoID) {
        this.movimentoID = movimentoID;
    }

    public Integer getMovimentoID() {
        return movimentoID;
    }

    public void setMovimentoID(Integer movimentoID) {
        this.movimentoID = movimentoID;
    }

    public Integer getUsuarioID() {
        return usuarioID;
    }

    public void setUsuarioID(Integer usuarioID) {
        this.usuarioID = usuarioID;
    }

    public Character getTipo() {
        return tipo;
    }

    public void setTipo(Character tipo) {
        this.tipo = tipo;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public Pessoa getPessoaID() {
        return pessoaID;
    }

    public void setPessoaID(Pessoa pessoaID) {
        this.pessoaID = pessoaID;
    }

    public Produto getProdutoID() {
        return produtoID;
    }

    public void setProdutoID(Produto produtoID) {
        this.produtoID = produtoID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (movimentoID != null ? movimentoID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Movimento)) {
            return false;
        }
        Movimento other = (Movimento) object;
        if ((this.movimentoID == null && other.movimentoID != null) || (this.movimentoID != null && !this.movimentoID.equals(other.movimentoID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Movimento[ movimentoID=" + movimentoID + " ]";
    }
    
}
