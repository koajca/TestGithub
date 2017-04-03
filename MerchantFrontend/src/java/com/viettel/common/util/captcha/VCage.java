/*
 * Copyright (C) 2013 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.common.util.captcha;

import com.github.cage.Cage;
import com.github.cage.image.ConstantColorGenerator;
import com.github.cage.image.EffectConfig;
import com.github.cage.image.Painter;
import com.github.cage.image.ScaleConfig;
import com.github.cage.token.RandomCharacterGeneratorFactory;
import com.github.cage.token.RandomTokenGenerator;
import java.awt.Color;
import java.util.Random;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since 07-01-2013
 * @version 1.0
 */
public class VCage extends com.github.cage.Cage {

    /**
     * Height of CAPTCHA image.
     */
    protected static final int HEIGHT = 50;
    /**
     * Width of CAPTCHA image.
     */
    protected static final int WIDTH = 120;
    /**
     * Character set supplied to the {@link RandomTokenGenerator} used by this
     * template.
     */
    protected static final char[] TOKEN_DEFAULT_CHARACTER_SET = RandomCharacterGeneratorFactory.ARABIC_NUMERALS;
//    protected static final char[] TOKEN_DEFAULT_CHARACTER_SET = (new String(
//            RandomCharacterGeneratorFactory.DEFAULT_DEFAULT_CHARACTER_SET)
//            .replaceAll("b|f|i|j|l|m|o|t", "")
//            + new String(
//            RandomCharacterGeneratorFactory.DEFAULT_DEFAULT_CHARACTER_SET)
//            .replaceAll("c|i|o", "").toUpperCase(Locale.ENGLISH) + new String(
//            RandomCharacterGeneratorFactory.ARABIC_NUMERALS).replaceAll(
//            "0|1|9", "")).toCharArray();
    /**
     * Minimum length of token.
     */
    protected static final int TOKEN_LEN_MIN = 4;
    /**
     * Maximum length of token is {@value #TOKEN_LEN_MIN} +
     * {@value #TOKEN_LEN_DELTA}.
     */
    protected static final int TOKEN_LEN_DELTA = 1;

    /**
     * Constructor.
     */
    public VCage() {
        this(new Random());
    }

    /**
     * Constructor.
     *     
* @param rnd object used for random value generation. Not null.
     */
    protected VCage(Random rnd) {
        super(new Painter(WIDTH, HEIGHT, null, null, new EffectConfig(true,
                true, true, false, new ScaleConfig(0.9f, 0.9f)), rnd), null,
                new ConstantColorGenerator(Color.BLACK), null,
                Cage.DEFAULT_COMPRESS_RATIO, new RandomTokenGenerator(rnd,
                new RandomCharacterGeneratorFactory(
                TOKEN_DEFAULT_CHARACTER_SET, null, rnd),
                TOKEN_LEN_MIN, TOKEN_LEN_DELTA), rnd);
    }
}
