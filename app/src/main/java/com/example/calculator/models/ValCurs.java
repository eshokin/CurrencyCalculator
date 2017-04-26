package com.example.calculator.models;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;


@Root(name = "ValCurs")
public class ValCurs {

    @ElementList(inline = true, name = "Valute")
    private List<Valute> valutes;

    public List<Valute> getValutes() {
        return valutes;
    }
}
