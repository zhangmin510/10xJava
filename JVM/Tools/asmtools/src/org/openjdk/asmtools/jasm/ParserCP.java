/*
 * Copyright (c) 1996, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.openjdk.asmtools.jasm;

import java.io.IOException;
import java.util.ArrayList;

import static org.openjdk.asmtools.jasm.JasmTokens.Token;
import static org.openjdk.asmtools.jasm.Tables.*;

/**
 * ParserCP
 *
 * ParseCP is a parser class owned by Parser.java. It is primarily responsible for parsing
 * the constant pool and constant declarations.
 */
public class ParserCP extends ParseBase {

    /**
     * local handles on the scanner, main parser, and the error reporting env
     */
    /**
     * Visitor object
     */
    private ParserCPVisitor pConstVstr;
    /**
     * counter of left braces
     */
    private int lbrace = 0;


    /**
     * main constructor
     *
     * @param scanner
     * @param parser
     * @param env
     */
    protected ParserCP(Scanner scanner, Parser parser, Environment env) {
        super.init(scanner, parser, env);
        pConstVstr = new ParserCPVisitor();
    }

    /**
     * ParserCPVisitor
     *
     * This inner class overrides a constant pool visitor to provide specific parsing
     * instructions (per method) for each type of Constant.
     *
     * Note: since the generic visitor throws no exceptions, this derived class tunnels
     * the exceptions, rethrown in the visitEcept method.
     */
    class ParserCPVisitor extends ConstantPool.CPTagVisitor<ConstantPool.ConstValue> {

        private IOException IOProb;
        private Scanner.SyntaxError SyProb;


        public ParserCPVisitor() {
            IOProb = null;
            SyProb = null;
        }

        //This is the entry point for a visitor that tunnels exceptions
        public ConstantPool.ConstValue visitExcept(ConstType tag) throws IOException, Scanner.SyntaxError {
            IOProb = null;
            SyProb = null;
            debugStr("------- [ParserCPVisitor.visitExcept]: ");
            ConstantPool.ConstValue ret = visit(tag);

            if (IOProb != null) {
                throw IOProb;
            }

            if (SyProb != null) {
                throw SyProb;
            }

            return ret;
        }

        @Override
        public ConstantPool.ConstValue visitUTF8(ConstType tag) {
            debugStr("------- [ParserCPVisitor.visitUTF8]: ");
            try {
                scanner.expect(Token.STRINGVAL);
            } catch (IOException e) {
                IOProb = e;
            }
            ConstantPool.ConstValue_String obj
                    = new ConstantPool.ConstValue_String(scanner.stringValue);
            return obj;
        }

        @Override
        public ConstantPool.ConstValue visitInteger(ConstType tag) {
            debugStr("------- [ParserCPVisitor.visitInteger]: ");
            ConstantPool.ConstValue_Integer obj;
            int v = 0;
            try {
                if (scanner.token == Token.BITS) {
                    scanner.scan();
                    scanner.inBits = true;
                }
                v = scanner.intValue * scanner.sign;
                scanner.expect(Token.INTVAL);
            } catch (IOException e) {
                IOProb = e;
            }
            obj = new ConstantPool.ConstValue_Integer(tag, new Integer(v));
            return obj;
        }

        @Override
        public ConstantPool.ConstValue visitLong(ConstType tag) {
            debugStr("------- [ParserCPVisitor.visitLong]: ");
            ConstantPool.ConstValue_Long obj = null;
            try {
                long v;
                if (scanner.token == Token.BITS) {
                    scanner.scan();
                    scanner.inBits = true;
                }
                switch (scanner.token) {
                    case INTVAL:
                        v = scanner.intValue;
                        break;
                    case LONGVAL:
                        v = scanner.longValue;
                        break;
                    default:
                        env.error(scanner.prevPos, "token.expected", "Integer");
                        throw new Scanner.SyntaxError();
                }
                obj = new ConstantPool.ConstValue_Long(tag, new Long(v * scanner.sign));
                scanner.scan();
            } catch (IOException e) {
                IOProb = e;
            } catch (Scanner.SyntaxError e) {
                SyProb = e;
            }
            return obj;
        }

        @Override
        public ConstantPool.ConstValue visitFloat(ConstType tag) {
            debugStr("------- [ParserCPVisitor.visitFloat]: ");
            ConstantPool.ConstValue_Integer obj = null;
            try {
                int v;
                float f;
                scanner.inBits = false;  // this needs to be initialized for each float!
                if (scanner.token == Token.BITS) {
                    scanner.scan();
                    scanner.inBits = true;
                }
i2f:            {
                    switch (scanner.token) {
                        case INTVAL:
                            if (scanner.inBits) {
                                v = scanner.intValue;
                                break i2f;
                            } else {
                                f = (float) scanner.intValue;
                                break;
                            }
                        case FLOATVAL:
                            f = scanner.floatValue;
                            break;
                        case DOUBLEVAL:
                            f = (float) scanner.doubleValue; // to be excluded?
                            break;
                        case INF:
                            f = Float.POSITIVE_INFINITY;
                            break;
                        case NAN:
                            f = Float.NaN;
                            break;
                        default:
                            env.traceln("token=" + scanner.token);
                            env.error(scanner.pos, "token.expected", "<Float>");
                            throw new Scanner.SyntaxError();
                    }
                    v = Float.floatToIntBits(f);
                }
                if (scanner.sign == -1) {
                    v = v ^ 0x80000000;
                }
                obj = new ConstantPool.ConstValue_Integer(tag, new Integer(v));
                scanner.scan();
            } catch (IOException e) {
                IOProb = e;
            } catch (Scanner.SyntaxError e) {
                SyProb = e;
            }
            return obj;
        }

        @Override
        public ConstantPool.ConstValue visitDouble(ConstType tag) {
            debugStr("------- [ParserCPVisitor.visitDouble]: ");
            ConstantPool.ConstValue_Long obj = null;
            try {
                long v;
                double d;
                if (scanner.token == Token.BITS) {
                    scanner.scan();
                    scanner.inBits = true;
                }
d2l:            {
                    switch (scanner.token) {
                        case INTVAL:
                            if (scanner.inBits) {
                                v = scanner.intValue;
                                break d2l;
                            } else {
                                d = (double) scanner.intValue;
                                break;
                            }
                        case LONGVAL:
                            if (scanner.inBits) {
                                v = scanner.longValue;
                                break d2l;
                            } else {
                                d = (double) scanner.longValue;
                                break;
                            }
                        case FLOATVAL:
                            d = scanner.floatValue;
                            break;
                        case DOUBLEVAL:
                            d = scanner.doubleValue;
                            break;
                        case INF:
                            d = Double.POSITIVE_INFINITY;
                            break;
                        case NAN:
                            d = Double.NaN;
                            break;
                        default:
                            env.error(scanner.pos, "token.expected", "Double");
                            throw new Scanner.SyntaxError();
                    }
                    v = Double.doubleToLongBits(d);
                }
                if (scanner.sign == -1) {
                    v = v ^ 0x8000000000000000L;
                }
                obj = new ConstantPool.ConstValue_Long(tag, new Long(v));
                scanner.scan();
            } catch (IOException e) {
                IOProb = e;
            } catch (Scanner.SyntaxError e) {
                SyProb = e;
            }
            return obj;
        }

        private ConstantPool.ConstCell visitName(ConstType tag) {
            debugStr("------- [ParserCPVisitor.visitName]: ");
            ConstantPool.ConstCell obj = null;
            try {
                obj = parser.parseName();
            } catch (IOException e) {
                IOProb = e;
            }
            return obj;
        }

        @Override
        public ConstantPool.ConstValue visitMethodtype(ConstType tag) {
            debugStr("------- [ParserCPVisitor.visitMethodtype]: ");
            ConstantPool.ConstValue_Cell obj = null;
            ConstantPool.ConstCell cell = visitName(tag);
            if (IOProb == null) {
                obj = new ConstantPool.ConstValue_Cell(tag, cell);
            }
            return obj;
        }

        @Override
        public ConstantPool.ConstValue visitString(ConstType tag) {
            debugStr("------- [ParserCPVisitor.visitString]: ");
            ConstantPool.ConstValue_Cell obj = null;
            ConstantPool.ConstCell cell = visitName(tag);
            if (IOProb == null) {
                obj = new ConstantPool.ConstValue_Cell(tag, cell);
            }
            return obj;
        }

        @Override
        public ConstantPool.ConstValue visitClass(ConstType tag) {
            debugStr("------- [ParserCPVisitor.visitClass]: ");
            ConstantPool.ConstValue_Cell obj = null;
            try {
                ConstantPool.ConstCell cell = parser.parseClassName(true);
                obj = new ConstantPool.ConstValue_Cell(tag, cell);
            } catch (IOException e) {
                IOProb = e;
            }
            return obj;
        }

        @Override
        public ConstantPool.ConstValue visitMethodhandle(ConstType tag) {
            debugStr("------- [ParserCPVisitor.visitMethodHandle]: ");
            ConstantPool.ConstValue_Pair obj = null;
            try {
                ConstantPool.ConstCell refCell;
                ConstantPool.ConstCell subtagCell;
                SubTag subtag;
                if (scanner.token == Token.INTVAL) {
                    // Handle explicit constant pool form
                    subtag = subtag(scanner.intValue);
                    subtagCell = new ConstantPool.ConstCell(subtag.value());
                    scanner.scan();
                    scanner.expect(Token.COLON);
                    if (scanner.token != Token.CPINDEX) {
                        env.traceln("token=" + scanner.token);
                        env.error(scanner.pos, "token.expected", "<CPINDEX>");
                        throw new Scanner.SyntaxError();
                    }
                    int cpx = scanner.intValue;
                    refCell = parser.pool.getCell(cpx);
                    scanner.scan();

                } else {
                    // normal JASM
                    subtag = parser.parseSubtag();
                    subtagCell = new ConstantPool.ConstCell(subtag.value());
                    scanner.expect(Token.COLON);
                    refCell = parser.parseMethodHandle(subtag);
                }
                obj = new ConstantPool.ConstValue_Pair(tag, subtagCell, refCell);
            } catch (IOException e) {
                IOProb = e;
            }
            return obj;
        }

        private ConstantPool.ConstValue_Pair visitMember(ConstType tag) {
            debugStr("------- [ParserCPVisitor.visitMember]: ");
            ConstantPool.ConstValue_Pair obj = null;
            try {
                Token prevtoken = scanner.token;
                ConstantPool.ConstCell firstName, ClassCell, NameCell, NapeCell;
                firstName = parser.parseClassName(false);
                if (scanner.token == Token.FIELD) { // DOT
                    scanner.scan();
                    if (prevtoken == Token.CPINDEX) {
                        ClassCell = firstName;
                    } else {
                        ClassCell = parser.pool.FindCell(ConstType.CONSTANT_CLASS, firstName);
                    }
                    NameCell = parser.parseName();
                } else {
                    // no class provided - assume current class
                    ClassCell = parser.cd.me;
                    NameCell = firstName;
                }
                if (scanner.token == Token.COLON) {
                    // name and type separately
                    scanner.scan();
                    NapeCell = parser.pool.FindCell(ConstType.CONSTANT_NAMEANDTYPE, NameCell, parser.parseName());
                } else {
                    // name and type as single name
                    NapeCell = NameCell;
                }
                obj = new ConstantPool.ConstValue_Pair(tag, ClassCell, NapeCell);
            } catch (IOException e) {
                IOProb = e;
            }
            return obj;
        }

        @Override
        public ConstantPool.ConstValue visitField(ConstType tag) {
            debugStr("------- [ParserCPVisitor.visitField]: ");
            return visitMember(tag);
        }

        @Override
        public ConstantPool.ConstValue visitMethod(ConstType tag) {
            debugStr("------- [ParserCPVisitor.visitMethod]: ");
            return visitMember(tag);
        }

        @Override
        public ConstantPool.ConstValue visitInterfacemethod(ConstType tag) {
            debugStr("------- [ParserCPVisitor.visitInterfacemethod]: ");
            return visitMember(tag);
        }

        @Override
        public ConstantPool.ConstValue visitNameandtype(ConstType tag) {
            debugStr("------- [ParserCPVisitor.visitNameandtype]: ");
            ConstantPool.ConstValue_Pair obj = null;
            try {
                ConstantPool.ConstCell NameCell = parser.parseName(), TypeCell;
                scanner.expect(Token.COLON);
                TypeCell = parser.parseName();
                obj = new ConstantPool.ConstValue_Pair(tag, NameCell, TypeCell);
            } catch (IOException e) {
                IOProb = e;
            }
            return obj;
        }

        @Override
        public ConstantPool.ConstValue_IndyPair visitInvokedynamic(ConstType tag) {
            debugStr("------- [ParserCPVisitor.visitInvokeDynamic]: ");
            ConstantPool.ConstValue_IndyPair obj = null;
            try {
                if (scanner.token == Token.INTVAL) {
                    // Handle explicit constant pool form
                    int bsmIndex = scanner.intValue;
                    scanner.scan();
                    scanner.expect(Token.COLON);
                    if (scanner.token != Token.CPINDEX) {
                        env.traceln("token=" + scanner.token);
                        env.error(scanner.pos, "token.expected", "<CPINDEX>");
                        throw new Scanner.SyntaxError();
                    }
                    int cpx = scanner.intValue;
                    scanner.scan();
                    // Put a placeholder in place of BSM.
                    // resolve placeholder after the attributes are scanned.
                    BootstrapMethodData bsmData = new BootstrapMethodData(bsmIndex);
                    obj = new ConstantPool.ConstValue_IndyPair(bsmData, parser.pool.getCell(cpx));

                } else {
                    // Handle full form
                    ConstantPool.ConstCell MHCell = parser.pool.FindCell(parseConstValue(ConstType.CONSTANT_METHODHANDLE));
                    scanner.expect(Token.COLON);
                    ConstantPool.ConstCell NapeCell = parser.pool.FindCell(parseConstValue(ConstType.CONSTANT_NAMEANDTYPE));

                    ArrayList<ConstantPool.ConstCell> bsm_args = new ArrayList<>(256);

                    for (boolean sep = false; scanner.token != Token.SEMICOLON; sep = true) {
                        if (sep) scanner.expect(Token.COMMA);
                        bsm_args.add(parseConstRef(null));
                        scanner.idValue = null; // Clear tag
                    }

                    BootstrapMethodData bsmData = new BootstrapMethodData(MHCell, bsm_args);
                    parser.cd.addBootstrapMethod(bsmData);
                    obj = new ConstantPool.ConstValue_IndyPair(bsmData, NapeCell);
                }
            } catch (IOException e) {
                IOProb = e;
            }
            return obj;
        }

        @Override
        public ConstantPool.ConstValue_CondyPair visitDynamic(ConstType tag) {
            debugStr("------- [ParserCPVisitor.visitDynamic]: ");
            ConstantPool.ConstValue_CondyPair obj = null;
            try {
                if (scanner.token == Token.INTVAL) {
                    // Handle explicit constant pool form
                    int bsmIndex = scanner.intValue;
                    scanner.scan();
                    scanner.expect(Token.COLON);
                    if (scanner.token != Token.CPINDEX) {
                        env.traceln("token=" + scanner.token);
                        env.error(scanner.pos, "token.expected", "<CPINDEX>");
                        throw new Scanner.SyntaxError();
                    }
                    int cpx = scanner.intValue;
                    scanner.scan();
                    // Put a placeholder in place of BSM.
                    // resolve placeholder after the attributes are scanned.
                    BootstrapMethodData bsmData = new BootstrapMethodData(bsmIndex);
                    obj = new ConstantPool.ConstValue_CondyPair(bsmData, parser.pool.getCell(cpx));

                } else {
                    // Handle full form
                    ConstantPool.ConstCell MHCell = parser.pool.FindCell(parseConstValue(ConstType.CONSTANT_METHODHANDLE));
                    scanner.expect(Token.COLON);
                    ConstantPool.ConstCell NapeCell = parser.pool.FindCell(parseConstValue(ConstType.CONSTANT_NAMEANDTYPE));
                    if(scanner.token == Token.LBRACE) {
                        ParserCP.this.lbrace++;
                        scanner.scan();
                    }
                    ArrayList<ConstantPool.ConstCell> bsm_args = new ArrayList<>(256);
                    while(true) {
                        if( ParserCP.this.lbrace > 0 ) {
                            if(scanner.token == Token.RBRACE ) {
                                ParserCP.this.lbrace--;
                                scanner.scan();
                                break;
                            } else if(scanner.token == Token.SEMICOLON) {
                                scanner.expect(Token.RBRACE);
                            }
                        } else if(scanner.token == Token.SEMICOLON) {
                            break;
                        }
                        if (scanner.token == Token.COMMA) {
                            scanner.scan();
                        }
                        bsm_args.add(parseConstRef(null));
                    }
                    if( ParserCP.this.lbrace == 0 ) {
                        scanner.check(Token.SEMICOLON);
                    }
                    BootstrapMethodData bsmData = new BootstrapMethodData(MHCell, bsm_args);
                    parser.cd.addBootstrapMethod(bsmData);
                    obj = new ConstantPool.ConstValue_CondyPair(bsmData, NapeCell);
                }
            } catch (IOException e) {
                IOProb = e;
            }
            return obj;
        }
    } // End Visitor

    /**
     * Parse CONSTVALUE
     */
    protected ConstantPool.ConstValue parseConstValue(ConstType tag) throws IOException, Scanner.SyntaxError {
        return pConstVstr.visitExcept(tag);
    }

    /**
     * Parse [TAG] CONSTVALUE
     */
    protected ConstantPool.ConstValue parseTagConstValue(ConstType defaultTag) throws Scanner.SyntaxError, IOException {
        return parseTagConstValue(defaultTag, null, false);
    }

    private ConstType scanConstByID(boolean ignoreKeywords) {
        ConstType tag = null;
        if (!ignoreKeywords) {
            ConstType tg = Tables.tag(scanner.idValue);
            if (tg != null) {
                tag = tg;
            }
            debugStr(" *^*^*^*^ [ParserCP.scanConst]: {TAG = " + (tg == null ? "null" : tg.toString()) + " ");
        }
        return tag;
    }

    private ConstType scanConstPrimVal() throws Scanner.SyntaxError, IOException {
        ConstType tag = null;
        switch (scanner.token) {
            case INTVAL:
                tag = ConstType.CONSTANT_INTEGER;
                break;
            case LONGVAL:
                tag = ConstType.CONSTANT_LONG;
                break;
            case FLOATVAL:
                tag = ConstType.CONSTANT_FLOAT;
                break;
            case DOUBLEVAL:
                tag = ConstType.CONSTANT_DOUBLE;
                break;
            case STRINGVAL:
            case BITS:
            case IDENT:
                tag = ConstType.CONSTANT_STRING;
                break;
            default:
                // problem - no constant value
                System.err.println("NEAR: " + scanner.token.printval());
                env.error(scanner.pos, "value.expected");
                throw new Scanner.SyntaxError();
        }
        return tag;
    }

    private void checkWrongTag(ConstType tag, ConstType defaultTag, ConstType default2Tag) throws Scanner.SyntaxError, IOException {
        if (defaultTag != null) {
            if (tag != defaultTag) {
                if (default2Tag == null) {
                    env.error("warn.wrong.tag", defaultTag.parseKey());
                } else if (tag != default2Tag) {
                    env.error("warn.wrong.tag2", defaultTag.parseKey(), default2Tag.parseKey());
                }
            }
        }
    }

    protected ConstantPool.ConstValue parseTagConstValue(ConstType defaultTag, ConstType default2Tag, boolean ignoreKeywords) throws Scanner.SyntaxError, IOException {
        debugScan(" *^*^*^*^ [ParserCP.parseTagConstValue]: Begin default_tag:  ignoreKeywords: " + (ignoreKeywords ? "true" : "false"));
        // Lookup the Tag from the scanner
        ConstType tag = scanConstByID(ignoreKeywords);
        debugStr(" *^*^*^*^ [ParserCP.parseTagConstValue]: {tag = " + tag + ", defaulttag = " + defaultTag + "} ");

        // If the scanned tag is null
        if (tag == null) {
            // and, if the expected tag is null
            if (defaultTag == null) {
                // return some other type of constant as the tag
                tag = scanConstPrimVal();
            } else {
                // otherwise, make the scanned-tag the same constant-type
                // as the expected tag.
                tag = defaultTag;
            }
        } else {
            // If the scanned tag is some constant type
            // and the scanned type does not equal the expected type
            checkWrongTag(tag, defaultTag, default2Tag);
            scanner.scan();
        }
        return parseConstValue(tag);
    } // end parseTagConstValue

    protected ConstantPool.ConstCell parseConstRef(ConstType defaultTag) throws Scanner.SyntaxError, IOException {
        return parseConstRef(defaultTag, null, false);
    }

    protected ConstantPool.ConstCell parseConstRef(ConstType defaultTag, ConstType default2Tag) throws Scanner.SyntaxError, IOException {
        return parseConstRef(defaultTag, default2Tag, false);
    }

    /**
     * Parse an instruction argument, one of: * #NUMBER, #NAME, [TAG] CONSTVALUE
     */
    protected ConstantPool.ConstCell parseConstRef(ConstType defaultTag,
            ConstType default2Tag,
            boolean ignoreKeywords) throws Scanner.SyntaxError, IOException {
        if (scanner.token == Token.CPINDEX) {
            int cpx = scanner.intValue;
            scanner.scan();
            return parser.pool.getCell(cpx);
        } else {
            ConstantPool.ConstValue ref = null;
            ref = parseTagConstValue(defaultTag, default2Tag, ignoreKeywords);
            return parser.pool.FindCell(ref);
        }
    } // end parseConstRef

}
