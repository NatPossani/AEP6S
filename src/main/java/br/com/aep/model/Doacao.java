package br.com.aep.model;

import java.util.ArrayList;
import java.util.List;

public class Doacao {

    private String codigo;
    private String doadorCodigo;
    private String instituicaoCodigo;
    private String dataRegistro;
    private String status;
    private List<ItemDoacao> itens;

    public Doacao() {
        this.itens = new ArrayList<>();
    }

    public Doacao(String codigo, String doadorCodigo, String instituicaoCodigo,
                  String dataRegistro, String status, List<ItemDoacao> itens) {
        this.codigo = codigo;
        this.doadorCodigo = doadorCodigo;
        this.instituicaoCodigo = instituicaoCodigo;
        this.dataRegistro = dataRegistro;
        this.status = status;
        this.itens = itens == null ? new ArrayList<>() : itens;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDoadorCodigo() {
        return doadorCodigo;
    }

    public void setDoadorCodigo(String doadorCodigo) {
        this.doadorCodigo = doadorCodigo;
    }

    public String getInstituicaoCodigo() {
        return instituicaoCodigo;
    }

    public void setInstituicaoCodigo(String instituicaoCodigo) {
        this.instituicaoCodigo = instituicaoCodigo;
    }

    public String getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(String dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ItemDoacao> getItens() {
        return itens;
    }

    public void setItens(List<ItemDoacao> itens) {
        this.itens = itens == null ? new ArrayList<>() : itens;
    }
}
