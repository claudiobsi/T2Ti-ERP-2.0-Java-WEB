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
package com.t2tierp.controller.lojavirtual;

import java.math.BigDecimal;
import java.util.List;

import br.com.uol.pagseguro.api.PagSeguro;
import br.com.uol.pagseguro.api.PagSeguroEnv;
import br.com.uol.pagseguro.api.checkout.CheckoutRegistrationBuilder;
import br.com.uol.pagseguro.api.checkout.RegisteredCheckout;
import br.com.uol.pagseguro.api.common.domain.ShippingType;
import br.com.uol.pagseguro.api.common.domain.builder.AddressBuilder;
import br.com.uol.pagseguro.api.common.domain.builder.PaymentItemBuilder;
import br.com.uol.pagseguro.api.common.domain.builder.PhoneBuilder;
import br.com.uol.pagseguro.api.common.domain.builder.SenderBuilder;
import br.com.uol.pagseguro.api.common.domain.builder.ShippingBuilder;
import br.com.uol.pagseguro.api.common.domain.enums.Currency;
import br.com.uol.pagseguro.api.common.domain.enums.State;
import br.com.uol.pagseguro.api.credential.Credential;
import br.com.uol.pagseguro.api.exception.PagSeguroBadRequestException;
import br.com.uol.pagseguro.api.exception.ServerError;
import br.com.uol.pagseguro.api.http.JSEHttpClient;
import br.com.uol.pagseguro.api.utils.logging.SimpleLoggerFactory;

import com.t2tierp.model.bean.cadastros.Produto;

public class IntegracaoPagSeguro {

    private static final String emailVendedor = "email@vendedor.com";
    private static final String tokenVendedor = "token_do_vendedor";

    public static String checkout(List<Produto> produtos) throws Exception {
        try {
            PagSeguro pagSeguro = PagSeguro.instance(new SimpleLoggerFactory(), new JSEHttpClient(),
                    Credential.sellerCredential(emailVendedor, tokenVendedor), PagSeguroEnv.SANDBOX);

            CheckoutRegistrationBuilder checkoutRegistration = new CheckoutRegistrationBuilder();
            checkoutRegistration.withCurrency(Currency.BRL)
                    .withCurrency(Currency.BRL) //
                    //.withExtraAmount(BigDecimal.ONE) //
                    .withReference("XXXXXX")
                    .withSender(new SenderBuilder()//
                            .withEmail("comprador@sandbox.pagseguro.com.br")//
                            .withName("Jose Comprador")
                            .withCPF("99999999999")
                            .withPhone(new PhoneBuilder()//
                                    .withAreaCode("99") //
                                    .withNumber("99999999"))) //
                    .withShipping(new ShippingBuilder()//
                            .withType(ShippingType.Type.SEDEX) //
                            .withCost(BigDecimal.TEN)//
                            .withAddress(new AddressBuilder() //
                                    .withPostalCode("99999999")
                                    .withCountry("BRA")
                                    .withState(State.DF)//
                                    .withCity("Cidade Exemplo")
                                    .withComplement("99o andar")
                                    .withDistrict("Jardim Internet")
                                    .withNumber("9999")
                                    .withStreet("Av. PagSeguro")));

            for (Produto p : produtos) {
                checkoutRegistration.addItem(new PaymentItemBuilder()//
                        .withId(p.getId().toString())//
                        .withDescription(p.getNome()) //
                        .withAmount(p.getValorVenda())//
                        .withQuantity(1)
                );
            }

            RegisteredCheckout registeredCheckout = pagSeguro.checkouts().register(checkoutRegistration);

            return registeredCheckout.getRedirectURL();
        }catch (PagSeguroBadRequestException e) {
            String erro = "";
        	for (ServerError error : e.getErrors().getErrors()) {
                erro += error.getMessage() + "\n";
            }
        	throw new Exception(erro);
        }catch (Exception e) {
        	throw e;
        }
    }
}