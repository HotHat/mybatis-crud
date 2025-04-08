package com.lyhux.mybatiscrud.builder.test;

import com.lyhux.mybatiscrud.builder.vendor.MysqlGrammar;
import org.junit.jupiter.api.BeforeEach;

public class MysqlGrammarTest extends GrammarTest {


    @BeforeEach
    public void setUp() throws Exception {
        grammar = new MysqlGrammar();
    }


}
