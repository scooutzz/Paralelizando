/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author rbgor
 */
@Entity
@Table(name = "PessoaFisica")
@NamedQueries({
    @NamedQuery(name = "PessoaFisica.findAll", query = "SELECT p FROM PessoaFisica p"),
    @NamedQuery(name = "PessoaFisica.findByPessoaID", query = "SELECT p FROM PessoaFisica p WHERE p.pessoaID = :pessoaID"),
    @NamedQuery(name = "PessoaFisica.findByCpf", query = "SELECT p FROM PessoaFisica p WHERE p.cpf = :cpf")})
public class PessoaFisica implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "PessoaID")
    private Integer pessoaID;
    @Column(name = "CPF")
    private String cpf;
    @JoinColumn(name = "Pessoa_PessoaID", referencedColumnName = "PessoaID")
    @ManyToOne(optional = false)
    private Pessoa pessoaPessoaID;

    public PessoaFisica() {
    }

    public PessoaFisica(Integer pessoaID) {
        this.pessoaID = pessoaID;
    }

    public Integer getPessoaID() {
        return pessoaID;
    }

    public void setPessoaID(Integer pessoaID) {
        this.pessoaID = pessoaID;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Pessoa getPessoaPessoaID() {
        return pessoaPessoaID;
    }

    public void setPessoaPessoaID(Pessoa pessoaPessoaID) {
        this.pessoaPessoaID = pessoaPessoaID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pessoaID != null ? pessoaID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PessoaFisica)) {
            return false;
        }
        PessoaFisica other = (PessoaFisica) object;
        if ((this.pessoaID == null && other.pessoaID != null) || (this.pessoaID != null && !this.pessoaID.equals(other.pessoaID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.PessoaFisica[ pessoaID=" + pessoaID + " ]";
    }
    
}
