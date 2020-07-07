/*
* The MIT License
* 
* Copyright: Copyright (C) 2014 T2Ti.COM
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
* 
* The author may be contacted at: t2ti.com@gmail.com
*
* @author Claudio de Barros (T2Ti.com)
* @version 2.0
*/
package com.t2tierp.model.bean.nfce;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "NFCE_RESOLUCAO")
public class NfceResolucao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "RESOLUCAO_TELA")
    private String resolucaoTela;
    @Column(name = "LARGURA")
    private Integer largura;
    @Column(name = "ALTURA")
    private Integer altura;
    @Column(name = "IMAGEM_TELA")
    private String imagemTela;
    @Column(name = "IMAGEM_MENU")
    private String imagemMenu;
    @Column(name = "IMAGEM_SUBMENU")
    private String imagemSubmenu;

    public NfceResolucao() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getResolucaoTela() {
        return resolucaoTela;
    }

    public void setResolucaoTela(String resolucaoTela) {
        this.resolucaoTela = resolucaoTela;
    }

    public Integer getLargura() {
        return largura;
    }

    public void setLargura(Integer largura) {
        this.largura = largura;
    }

    public Integer getAltura() {
        return altura;
    }

    public void setAltura(Integer altura) {
        this.altura = altura;
    }

    public String getImagemTela() {
        return imagemTela;
    }

    public void setImagemTela(String imagemTela) {
        this.imagemTela = imagemTela;
    }

    public String getImagemMenu() {
        return imagemMenu;
    }

    public void setImagemMenu(String imagemMenu) {
        this.imagemMenu = imagemMenu;
    }

    public String getImagemSubmenu() {
        return imagemSubmenu;
    }

    public void setImagemSubmenu(String imagemSubmenu) {
        this.imagemSubmenu = imagemSubmenu;
    }

    @Override
    public String toString() {
        return "com.t2tierp.model.bean.nfce.NfceResolucao[id=" + id + "]";
    }

}
