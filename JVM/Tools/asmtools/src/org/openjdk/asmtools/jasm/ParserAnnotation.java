/*
 * Copyright (c) 1996, 2017, Oracle and/or its affiliates. All rights reserved.
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

import static org.openjdk.asmtools.jasm.TypeAnnotationUtils.*;
import static org.openjdk.asmtools.jasm.JasmTokens.*;
import static org.openjdk.asmtools.jasm.ConstantPool.*;
import static org.openjdk.asmtools.jasm.Tables.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * ParserAnnotation
 *
 * ParserAnnotation is a parser class owned by Parser.java. It is primarily responsible
 * for parsing Annotations (for classes, methods or fields).
 *
 * ParserAnnotation can parse the different types of Annotation Attributes:
 * Runtime(In)Visible Annotations (JDK 6+) Default Annotations (JDK 6+)
 * Runtime(In)VisibleParameter Annotations (JDK 7+) Runtime(In)VisibleType Annotations
 * (JSR308, JDK8+)
 */
public class ParserAnnotation extends ParseBase {

    /*-------------------------------------------------------- */
    /* Annotation Inner Classes */
    /**
     * AnnotationElemValue
     *
     * Used to store Annotation values
     */
    class AnnotationElemValue implements Data {

        AnnotationData annotation;

        public AnnotationElemValue(AnnotationData annotation) {
            this.annotation = annotation;
        }

        @Override
        public void write(CheckedDataOutputStream out) throws IOException {
            out.writeByte('@');
            annotation.write(out);
        }

        @Override
        public int getLength() {
            return 1 + annotation.getLength();
        }
    }

    /**
     * ClassElemValue
     *
     * Annotation Element value referring to a class
     */
    class ClassElemValue implements Data {

        ConstCell indx;

        public ClassElemValue(ConstCell indx) {
            this.indx = indx;
        }

        @Override
        public void write(CheckedDataOutputStream out) throws IOException {
            out.writeByte('c');
            indx.write(out);
        }

        @Override
        public int getLength() {
            return 3;
        }
    }

    /**
     * ArrayElemValue
     *
     * Annotation Element value referring to an Array
     */
    class ArrayElemValue implements Data {

        ArrayList<Data> elemValues;
        int arrayLength = 0;

        public ArrayElemValue() {
            this.elemValues = new ArrayList<>();
        }

        void add(Data elemValue) {
            elemValues.add(elemValue);
            arrayLength += elemValue.getLength();
        }

        @Override
        public void write(CheckedDataOutputStream out) throws IOException {
            out.writeByte('[');
            out.writeShort(elemValues.size());

            for (Data eval : elemValues) {
                eval.write(out);
            }
        }

        @Override
        public int getLength() {
            return 3 + arrayLength;
        }
    }

    /**
     * ConstElemValue
     *
     * Annotation Element value referring to a Constant
     */
    class ConstElemValue implements Data {

        char tag;
        ConstCell indx;

        public ConstElemValue(char tag, ConstCell indx) {
            this.tag = tag;
            this.indx = indx;
        }

        @Override
        public void write(CheckedDataOutputStream out) throws IOException {
            out.writeByte(tag);
            indx.write(out);
        }

        @Override
        public int getLength() {
            return 3;
        }
    }

    /**
     * EnumElemValue
     *
     * Element Value for Enums
     */
    class EnumElemValue implements Data {

        ConstCell type;
        ConstCell value;

        public EnumElemValue(ConstCell type, ConstCell value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public void write(CheckedDataOutputStream out) throws IOException {
            out.writeByte('e');
            type.write(out);
            value.write(out);
        }

        @Override
        public int getLength() {
            return 5;
        }
    }

    /*-------------------------------------------------------- */
    /* Annotation Parser Fields */
    /**
     * local handles on the scanner, main parser, and the error reporting env
     */
    private static TTVis ttVisitor;
    /*-------------------------------------------------------- */

    /**
     * main constructor
     *
     * @param scanner
     * @param parser
     * @param env
     */
    protected ParserAnnotation(Scanner scanner, Parser parser, Environment env) {
        super.init(scanner, parser, env);
        ttVisitor = new TTVis();
        ttVisitor.init(env, scanner);
    }

    protected void scanParamName(int totalParams, int paramNum, MethodData curMethod) throws IOException {
        debugScan(" - - - > [ParserAnnotation.scanParamName]: Begin ");
        scanner.scan();
        scanner.expect(Token.LBRACE);
        // First scan the Name (String, or CPX to name)
        ConstCell nameCell = null;
        if ((scanner.token == Token.IDENT) || scanner.checkTokenIdent()) {
            // Got a Class Name
            nameCell = parser.parseName();
        } else if (scanner.token == Token.CPINDEX) {
            int cpx = scanner.intValue;
            nameCell = parser.pool.getCell(cpx);
            // check the constant
            ConstValue nameCellValue = nameCell.ref;
            if (!(nameCellValue instanceof ConstValue_String)) {
                // throw an error
                env.error(scanner.pos, "paramname.constnum.invaltype", cpx);
                throw new Scanner.SyntaxError();
            }

        } else {
            // throw scan error - unexpected token
            env.error(scanner.pos, "paramname.token.unexpected", scanner.stringValue);
            throw new Scanner.SyntaxError();
        }

        // Got the name cell. Next, scan the access flags
        int mod = parser.scanModifiers();

        scanner.expect(Token.RBRACE);

        curMethod.addMethodParameter(totalParams, paramNum, nameCell, mod);

        debugScan(" - - - > [ParserAnnotation.scanParamName]: End ");
    }

    /**
     * scanAnnotations
     *
     * The main entry for parsing an annotation list.
     *
     * @return An ArrayList of parsed annotations
     * @throws IOException
     */
    protected ArrayList<AnnotationData> scanAnnotations() throws IOException {
        ArrayList<AnnotationData> annttns = new ArrayList<>();

        while (scanner.token == Token.ANNOTATION) {
            if (isAnnotationToken(scanner.stringValue)) {
                annttns.add(parseAnnotation());
            } else if (isTypeAnnotationToken(scanner.stringValue)) {
                annttns.add(parseTypeAnnotation());
            } else {
                return null;
            }
        }

        if (annttns.size() > 0) {
            return annttns;
        } else {
            return null;
        }
    }

    /**
     * isAnnotation
     *
     * examines the beginning of a string to see if it starts with an annotation character
     *
     * @param str
     * @return True if the string starts with an annotation char.
     */
    protected boolean isAnnotation(String str) {
        return (str.startsWith("@"));
    }

    /**
     * isAnnotationToken
     *
     * examines the beginning of a string to see if it starts with an annotation
     * characters ('@+' = visible annotation, '@-' = invisible).
     *
     * @param str
     * @return True if the string starts with an annotation char.
     */
    protected boolean isAnnotationToken(String str) {
        return (str.startsWith("@+") || str.startsWith("@-"));
    }

    /**
     * isTypeAnnotationToken
     *
     * examines the beginning of a string to see if it starts with type annotation
     * characters ('@T+' = visible type annotation, '@T-' = invisible).
     *
     * @param str
     * @return True if the string starts with an annotation char.
     */
    protected boolean isTypeAnnotationToken(String str) {
        return (str.startsWith("@T+") || str.startsWith("@T-"));
    }

    /**
     * isInvisibleAnnotToken
     *
     * examines the end of an annotation token to determine visibility ('+' = visible
     * annotation, '-' = invisible).
     *
     * @param str
     * @return True if the token implies invisible annotation.
     */
    protected boolean isInvisibleAnnotToken(String str) {
        return (str.endsWith("-"));
    }

    /**
     * parseDefaultAnnotation
     *
     * parses a default Annotation attribute
     *
     * @return the parsed Annotation Attribute
     * @throws org.openjdk.asmtools.jasm.Scanner.SyntaxError
     * @throws IOException
     */
    protected DefaultAnnotationAttr parseDefaultAnnotation() throws Scanner.SyntaxError, IOException {
        scanner.scan();
        DefaultAnnotationAttr attr = null;
        Data value = null;
        scanner.expect(Token.LBRACE);

        if ((scanner.token != Token.EOF) && (scanner.token != Token.RBRACE)) {
            value = scanAnnotationData("default");
        }
        scanner.expect(Token.RBRACE);
        attr = new DefaultAnnotationAttr(parser.cd,
                AttrTag.ATT_AnnotationDefault.parsekey(),
                value);
        return attr;
    }

    /**
     * parseParamAnnots
     *
     * Parses Parameter Annotations attributes.
     *
     * @param _totalParams
     * @param curMethod
     * @throws org.openjdk.asmtools.jasm.Scanner.SyntaxError
     * @throws IOException
     */
    protected void parseParamAnnots(int _totalParams, MethodData curMethod) throws Scanner.SyntaxError, IOException {
        debugScan(" - - - > [ParserAnnotation.parseParamAnnots]: Begin, totalParams =  " + _totalParams + " ");
        // _The method thinks there are N+1 params in the signature
        // (N = total params in the call list) + 1 (return value)
        int totalParams = _totalParams - 1;
        TreeMap<Integer, ArrayList<AnnotationData>> pAnnots = new TreeMap<>();

        while (scanner.token == Token.INTVAL) {
            // Create the Parameter Array for  Param Annotations

            // Do something with Parameter annotations
            // --------------------
            // First - validate that the parameter number (integer)
            // (eg >= 0, < numParams, and param num is not previously set)
            int paramNum = scanner.intValue;
            Integer iParamNum = Integer.valueOf(paramNum);
            if (paramNum < 0 || paramNum >= totalParams) {
                //invalid Parameter number.  Throw an error.
                env.error(scanner.pos, "invalid.paramnum", paramNum);
            }
            if (pAnnots.get(iParamNum) != null) {
                // paramter is already populated with annotations/pnames, Throw an error.
                env.error(scanner.pos, "duplicate.paramnum", paramNum);
            }
            // 2nd - Parse the COLON (invalid if not present)
            scanner.scan();
            scanner.expect(Token.COLON);

            // 3rd - parse either an optional ParamName, or a list of annotations
            if (scanner.token == Token.PARAM_NAME) {
                //parse the ParamName
                scanParamName(_totalParams, iParamNum, curMethod);
            }

            // 4th - parse each Annotation (followed by comma, followed by annotation
            //       assign array of annotations to param array
            if (scanner.token == Token.ANNOTATION) {
                ArrayList<AnnotationData> pAnnot = scanAnnotations();
                pAnnots.put(iParamNum, pAnnot);

                for (AnnotationData data : pAnnot) {
                    curMethod.addParamAnnotation(totalParams, paramNum, data);
                }
            }

        }
    }

    /* ************************* Private Members  *************************** */
    /**
     * parseTypeAnnotation
     *
     * parses an individual annotation.
     *
     * @return a parsed annotation.
     * @throws IOException
     */
    private AnnotationData parseTypeAnnotation() throws Scanner.SyntaxError, IOException {
        boolean invisible = isInvisibleAnnotToken(scanner.stringValue);
        scanner.scan();
        debugScan("     [ParserAnnotation.parseTypeAnnotation]: id = " + scanner.stringValue + " ");
        String annoName = "L" + scanner.stringValue + ";";
        TypeAnnotationData anno = new TypeAnnotationData(parser.pool.FindCellAsciz(annoName), invisible);
        scanner.scan();
        debugScan("     [ParserAnnotation.parseTypeAnnotation]:new type annotation: " + annoName + " ");

        scanner.expect(Token.LBRACE);

        // Scan the usual annotation data
        _scanAnnotation(anno);

        // scan the Target
        _scanTypeTarget(anno);

        // scan the Location
        _scanTargetPath(anno);

        scanner.expect(Token.RBRACE);
        return anno;
    }

    /**
     * scanAnnotation
     *
     * parses an individual annotation.
     *
     * @return a parsed annotation.
     * @throws IOException
     */
    private AnnotationData parseAnnotation() throws Scanner.SyntaxError, IOException {
        debugScan(" - - - > [ParserAnnotation.parseAnnotation]: Begin ");
        boolean invisible = isInvisibleAnnotToken(scanner.stringValue);
        scanner.scan();
        String annoName = "L" + scanner.stringValue + ";";

        AnnotationData anno = new AnnotationData(parser.pool.FindCellAsciz(annoName), invisible);
        scanner.scan();
        debugScan("[ParserAnnotation.parseAnnotation]: new annotation: " + annoName);
        _scanAnnotation(anno);

        return anno;
    }

    /**
     * _scanAnnotation
     *
     * parses an individual annotation-data.
     *
     * @return a parsed annotation.
     * @throws IOException
     */
    private void _scanAnnotation(AnnotationData annotData) throws Scanner.SyntaxError, IOException {
        debugScan(" - - - > [ParserAnnotation._scanAnnotation]: Begin");
        scanner.expect(Token.LBRACE);

        while ((scanner.token != Token.EOF) && (scanner.token != Token.RBRACE)) {
            ConstCell nameCell = parser.parseName();
            scanner.expect(Token.ASSIGN);

            ConstValue cellref = nameCell.ref;
            if (cellref.tag != ConstType.CONSTANT_UTF8) {
                throw new Scanner.SyntaxError();
            }
            String name = ((ConstValue_String) cellref)._toString();
            debugScan(" - - - > [ParserAnnotation._scanAnnotation]: Annot - Field Name: " + name);
            Data data = scanAnnotationData(name);
            annotData.add(new AnnotationData.ElemValuePair(nameCell, data));

            // consume tokens inbetween annotation fields
            if (scanner.token == Token.COMMA) {
                scanner.scan();
            }
        }
        scanner.expect(Token.RBRACE);
    }

    /**
     * _scanAnnotation
     *
     * parses an individual annotation-data.
     *
     * @return a parsed annotation.
     * @throws IOException
     */
    private void _scanTypeTarget(TypeAnnotationData annotData) throws Scanner.SyntaxError, IOException {
        debugScan("     [ParserAnnotation._scanTypeTarget]: Begin ");
        scanner.expect(Token.LBRACE);

        //Scan the target_type and the target_info
        scanner.expect(Token.IDENT);
        debugScan("     [ParserAnnotation._scanTypeTarget]: TargetType: " + scanner.idValue);
        TypeAnnotationUtils.TargetType tt = TypeAnnotationUtils.getTargetType(scanner.idValue);
        if (tt == null) {
            env.error(scanner.pos, "incorrect.typeannot.target", scanner.idValue);
            throw new Scanner.SyntaxError();
        }

        debugScan("     [ParserAnnotation._scanTypeTarget]: Got TargetType: " + tt);

        if (ttVisitor.scanner == null) {
            ttVisitor.scanner = scanner;
        }
        ttVisitor.visitExcept(tt);

        annotData.targetInfo = ttVisitor.getTargetInfo();
        annotData.targetType = tt;
        debugScan("     [ParserAnnotation._scanTypeTarget]: Got TargetInfo: " + annotData.targetInfo);

        scanner.expect(Token.RBRACE);
    }

    /**
     * TTVis
     *
     * Target Type visitor, used for constructing the target-info within a type
     * annotation. visitExcept() is the entry point. ti is the constructed target info.
     */
    private static class TTVis extends TypeAnnotationUtils.TypeAnnotationTargetVisitor {

        private TypeAnnotationUtils.TargetInfo ti;
        private IOException IOProb;
        private Scanner.SyntaxError SyProb;
        private Scanner scanner;
        private Environment env;

        public TTVis() {
            super();
            reset();
        }

        public void init(Environment en, Scanner scn) {
            if (scanner == null) {
                scanner = scn;
            }
            if (env == null) {
                env = en;
            }
        }

        public final void reset() {
            ti = null;
            IOProb = null;
            SyProb = null;
        }

        //This is the entry point for a visitor that tunnels exceptions
        public void visitExcept(TypeAnnotationUtils.TargetType tt) throws IOException, Scanner.SyntaxError {
            IOProb = null;
            SyProb = null;
            ti = null;

            visit(tt);

            if (IOProb != null) {
                throw IOProb;
            }

            if (SyProb != null) {
                throw SyProb;
            }
        }

        public TypeAnnotationUtils.TargetInfo getTargetInfo() {
            return ti;
        }

        // this fn gathers intvals, and tunnels any exceptions thrown by
        // the scanner
        private int scanIntVal(TypeAnnotationUtils.TargetType tt) {
            int ret = -1;
            if (scanner.token == Token.INTVAL) {
                ret = scanner.intValue;
                try {
                    scanner.scan();
                } catch (IOException e) {
                    IOProb = e;
                } catch (Scanner.SyntaxError e) {
                    SyProb = e;
                }
            } else {
                env.error(scanner.pos, "incorrect.typeannot.targtype.int", tt.parseKey(), scanner.token);
                SyProb = new Scanner.SyntaxError();
            }
            return ret;
        }

        // this fn gathers intvals, and tunnels any exceptions thrown by
        // the scanner
        private String scanStringVal(TypeAnnotationUtils.TargetType tt) {
            String ret = "";
            if (scanner.token == Token.STRINGVAL) {
                ret = scanner.stringValue;
                try {
                    scanner.scan();
                } catch (IOException e) {
                    IOProb = e;
                } catch (Scanner.SyntaxError e) {
                    SyProb = e;
                }
            } else {
                env.error(scanner.pos, "incorrect.typeannot.targtype.string", tt.parseKey(), scanner.token);
                SyProb = new Scanner.SyntaxError();
            }
            return ret;
        }

        // this fn gathers intvals, and tunnels any exceptions thrown by
        // the scanner
        private void scanBrace(boolean left) {
            try {
                scanner.expect(left ? Token.LBRACE : Token.RBRACE);
            } catch (IOException e) {
                IOProb = e;
            } catch (Scanner.SyntaxError e) {
                SyProb = e;
            }
        }

        private boolean error() {
            return IOProb != null || SyProb != null;
        }

        @Override
        public void visit_type_param_target(TypeAnnotationUtils.TargetType tt) {
            env.traceln("Type Param Target: ");
            int byteval = scanIntVal(tt); // param index
            if (!error()) {
                ti = new TypeAnnotationUtils.typeparam_target(tt, byteval);
            }
        }

        @Override
        public void visit_supertype_target(TypeAnnotationUtils.TargetType tt) {
            env.traceln("SuperType Target: ");
            int shortval = scanIntVal(tt); // type index
            if (!error()) {
                ti = new TypeAnnotationUtils.supertype_target(tt, shortval);
            }
        }

        @Override
        public void visit_typeparam_bound_target(TypeAnnotationUtils.TargetType tt) {
            env.traceln("TypeParam Bound Target: ");
            int byteval1 = scanIntVal(tt); // param index
            if (error()) {
                return;
            }
            int byteval2 = scanIntVal(tt); // bound index
            if (error()) {
                return;
            }
            ti = new TypeAnnotationUtils.typeparam_bound_target(tt, byteval1, byteval2);
        }

        @Override
        public void visit_empty_target(TypeAnnotationUtils.TargetType tt) {
            env.traceln("Empty Target: ");
            if (!error()) {
                ti = new TypeAnnotationUtils.empty_target(tt);
            }
        }

        @Override
        public void visit_methodformalparam_target(TypeAnnotationUtils.TargetType tt) {
            env.traceln("MethodParam Target: ");
            int byteval = scanIntVal(tt); // param index
            if (!error()) {
                ti = new TypeAnnotationUtils.methodformalparam_target(tt, byteval);
            }
        }

        @Override
        public void visit_throws_target(TypeAnnotationUtils.TargetType tt) {
            env.traceln("Throws Target: ");
            int shortval = scanIntVal(tt); // exception index
            if (!error()) {
                ti = new TypeAnnotationUtils.throws_target(tt, shortval);
            }
        }

        @Override
        public void visit_localvar_target(TypeAnnotationUtils.TargetType tt) {
            env.traceln("LocalVar Target: ");
            TypeAnnotationUtils.localvar_target locvartab = new TypeAnnotationUtils.localvar_target(tt, 0);
            ti = locvartab;

            while ((scanner.token != Token.EOF) && (scanner.token != Token.RBRACE)) {
                // consume the left brace
                scanBrace(true);
                if (error()) {
                    return;
                }
                // scan the local var triple
                int shortval1 = scanIntVal(tt); // startPC
                if (error()) {
                    return;
                }
                int shortval2 = scanIntVal(tt); // length
                if (error()) {
                    return;
                }
                int shortval3 = scanIntVal(tt); // CPX
                locvartab.addEntry(shortval1, shortval2, shortval3);
                scanBrace(false);
                if (error()) {
                    return;
                }
            }
        }

        @Override
        public void visit_catch_target(TypeAnnotationUtils.TargetType tt) {
            env.traceln("Catch Target: ");
            int shortval = scanIntVal(tt); // catch index

            ti = new TypeAnnotationUtils.catch_target(tt, shortval);
        }

        @Override
        public void visit_offset_target(TypeAnnotationUtils.TargetType tt) {
            env.traceln("Offset Target: ");
            int shortval = scanIntVal(tt); // offset index
            if (!error()) {
                ti = new TypeAnnotationUtils.offset_target(tt, shortval);
            }
        }

        @Override
        public void visit_typearg_target(TypeAnnotationUtils.TargetType tt) {
            env.traceln("TypeArg Target: ");
            int shortval = scanIntVal(tt); // offset
            if (error()) {
                return;
            }
            int byteval = scanIntVal(tt); // type index
            if (error()) {
                return;
            }
            ti = new TypeAnnotationUtils.typearg_target(tt, shortval, byteval);
        }

    }

    /**
     * _scanTypeLocation
     *
     * parses an individual annotation-data.
     *
     * @return a parsed annotation.
     * @throws IOException
     */
    private void _scanTargetPath(TypeAnnotationData annotData) throws Scanner.SyntaxError, IOException {

        ArrayList<TypePathEntry> targPath = new ArrayList<>();
        // parse the location info
        scanner.expect(Token.LBRACE);

        while ((scanner.token != Token.EOF) && (scanner.token != Token.RBRACE)) {
            TypePathEntry tpe = _scanTypePathEntry();

            scanner.scan();
            // throw away comma
            if (scanner.token == Token.COMMA) {
                scanner.scan();
            }
        }
        scanner.expect(Token.RBRACE);
        annotData.targetPath = targPath;
    }

    /**
     * _scanTypeLocation
     *
     * parses an individual annotation-data.
     *
     * @return a parsed annotation.
     * @throws IOException
     */
    private TypePathEntry _scanTypePathEntry() throws Scanner.SyntaxError, IOException {
        TypePathEntry tpe;

        if ((scanner.token != Token.EOF) && (scanner.token == Token.STRINGVAL)) {
            String pathEntryKey = scanner.stringValue;
            PathKind pk = pathKind(pathEntryKey);
            if (pk == null) {
                // unexpected Type Path
                env.error(scanner.pos, "incorrect.typeannot.pathentry", scanner.stringValue);
                throw new Scanner.SyntaxError();
            }
            if (pk == PathKind.ITHARG_PARAMETERTYPE) {
                //
                // need to scan the index
                // Take the form:  INNER_TYPE(#)
                scanner.expect(Token.LPAREN);
                int index = 0;
                if ((scanner.token != Token.EOF) && (scanner.token == Token.INTVAL)) {
                    index = scanner.intValue;
                } else {
                    // incorrect Arg index
                    env.error(scanner.pos, "incorrect.typeannot.pathentry.argindex", scanner.token);
                    throw new Scanner.SyntaxError();
                }

                scanner.expect(Token.RPAREN);
                tpe = new TypePathEntry(pk.key(), (char) index);
            } else {
                tpe = new TypePathEntry(pk.key(), (char) 0);
            }
        } else {
            // unexpected Type Path
            env.error(scanner.pos, "incorrect.typeannot.pathentry", scanner.token);
            throw new Scanner.SyntaxError();
        }

        return tpe;
    }

    /**
     * scanAnnotationArray
     *
     * Scans an Array of annotations.
     *
     * @param name Name of the annotation
     * @return Array Element
     * @throws IOException if scanning errors exist
     */
    private ArrayElemValue scanAnnotationArray(String name) throws IOException {
        scanner.scan();
        ArrayElemValue arrayElem = new ArrayElemValue();

        while ((scanner.token != Token.EOF) && (scanner.token != Token.RBRACE)) {
            Data data = scanAnnotationData(name + " {}");
            arrayElem.add(data);

            // consume tokens inbetween annotation fields
            if (scanner.token == Token.COMMA) {
                scanner.scan();
            }
        }

        scanner.expect(Token.RBRACE);
        return arrayElem;
    }

    /**
     * scanAnnotationEnum
     *
     * Scans an annotation enum val.
     *
     * @param name Annotation Name
     * @return Constant element value for the Class Annotation.
     * @throws IOException
     */
    private Data scanAnnotationClass(String name) throws IOException {
        Data constVal = null;
        // scan the next identifier.
        // if it is an Ident, consume it as the class name.
        scanner.scan();
        switch (scanner.token) {
            case IDENT:
                env.traceln("[AnnotationParser.scanAnnotationData]:: Constant Class Field: " + name + " = " + scanner.stringValue);
                //need to encode the stringval as an (internal) descriptor.
                String desc = parser.encodeClassString(scanner.stringValue);

                // note: for annotations, a class field points to a string with the class descriptor.
                constVal = new ConstElemValue('c', parser.pool.FindCellAsciz(desc));
                scanner.scan();
                break;
            case CPINDEX:
                // could be a reference to a class name
                env.traceln("[AnnotationParser.scanAnnotationData]:: Constant Class Field: " + name + " = " + scanner.stringValue);
                Integer ConstNmCPX = new Integer(scanner.stringValue);
                constVal = new ClassElemValue(parser.pool.getCell(ConstNmCPX));
                scanner.scan();
                break;
            default:
                env.error(scanner.pos, "incorrect.annot.class", scanner.stringValue);
                throw new Scanner.SyntaxError();
        }

        return constVal;
    }

    /**
     * scanAnnotationEnum
     *
     * Scans an annotation enum val.
     *
     * @param name Annotation Name
     * @return Enumeration Element Value
     * @throws IOException for scanning errors.
     */
    private EnumElemValue scanAnnotationEnum(String name) throws IOException {
        scanner.scan();
        EnumElemValue enumval = null;
        switch (scanner.token) {
            case IDENT:
                // could be a string identifying enum class and name
                String enumClassName = scanner.stringValue;
                scanner.scan();
                // could be a string identifying enum class and name
                switch (scanner.token) {
                    case IDENT:
                        // could be a string identifying enum class and name
                        String enumTypeName = scanner.stringValue;
                        env.traceln("[AnnotationParser.scanAnnotationEnum]:: Constant Enum Field: " + name + " = " + enumClassName + " " + enumTypeName);
                        String encodedClass = parser.encodeClassString(enumClassName);
                        ConstElemValue classConst = new ConstElemValue('s', parser.pool.FindCellAsciz(encodedClass));
                        ConstElemValue typeConst = new ConstElemValue('s', parser.pool.FindCellAsciz(enumTypeName));
                        enumval = new EnumElemValue(classConst.indx, typeConst.indx);
                        scanner.scan();
                        break;

                    default:
                        env.error(scanner.pos, "incorrect.annot.enum", scanner.stringValue);
                        throw new Scanner.SyntaxError();
                }
                break;
            case CPINDEX:
                Integer typeNmCPX = new Integer(scanner.stringValue);
                scanner.scan();
                //need two indexes to form a proper enum
                switch (scanner.token) {
                    case CPINDEX:
                        Integer ConstNmCPX = new Integer(scanner.stringValue);
                        env.traceln("[AnnotationParser.scanAnnotationEnum]:: Enumeration Field: " + name + " = #" + typeNmCPX + " #" + ConstNmCPX);
                        enumval = new EnumElemValue(parser.pool.getCell(typeNmCPX), parser.pool.getCell(ConstNmCPX));
                        scanner.scan();
                        break;
                    default:
                        env.error(scanner.pos, "incorrect.annot.enum.cpx");
                        throw new Scanner.SyntaxError();
                }
                break;
        }

        return enumval;
    }

    /**
     * scanAnnotationData
     *
     * parses the internals of an annotation.
     *
     * @param name Annotation Name
     * @return a Data data structure containing the annotation data.
     * @throws IOException for scanning errors.
     */
    private Data scanAnnotationData(String name) throws IOException {
        Data data = null;
        switch (scanner.token) {
            // This handles the Annotation types (as normalized in the constant pool)
            // Some primitive types (Boolean, char, short, byte) are identified by a keyword.
            case INTVAL:
                env.traceln("[AnnotationParser.scanAnnotationData]:: Integer Field: " + name + " = " + scanner.intValue);
                data = new ConstElemValue('I', parser.pool.FindCell(ConstType.CONSTANT_INTEGER, new Integer(scanner.intValue)));
                scanner.scan();
                break;
            case DOUBLEVAL:
                env.traceln("[AnnotationParser.scanAnnotationData]:: Double Field: " + name + " = " + scanner.doubleValue);
                double dval = new Double(scanner.doubleValue);
                long ivdal = Double.doubleToLongBits(dval);
                Long val = new Long(ivdal);
                data = new ConstElemValue('D', parser.pool.FindCell(ConstType.CONSTANT_DOUBLE, val));
                scanner.scan();
                break;
            case FLOATVAL:
                env.traceln("[AnnotationParser.scanAnnotationData]:: Float Field: " + name + " = " + scanner.floatValue);
                float fval = new Float(scanner.floatValue);
                int ifval = Float.floatToIntBits(fval);
                Integer val1 = new Integer(ifval);
                data = new ConstElemValue('F', parser.pool.FindCell(ConstType.CONSTANT_FLOAT, val1));
                scanner.scan();
                break;
            case LONGVAL:
                env.traceln("[AnnotationParser.scanAnnotationData]:: Long Field: " + name + " = " + scanner.longValue);
                data = new ConstElemValue('J', parser.pool.FindCell(ConstType.CONSTANT_LONG, new Long(scanner.longValue)));
                scanner.scan();
                break;
            case STRINGVAL:
                env.traceln("[AnnotationParser.scanAnnotationData]:: String Field: " + name + " = " + scanner.stringValue);
                data = new ConstElemValue('s', parser.pool.FindCellAsciz(scanner.stringValue));
                scanner.scan();
                break;
            case CLASS:
                env.traceln("[AnnotationParser.scanAnnotationData]:: Class) keyword: " + scanner.stringValue);
                data = scanAnnotationClass(name);
                break;
            case ENUM:
                // scan the next two identifiers (eg ident.ident), or 2 CPRefs.
                // if it is an Ident, use consume it as the class name.
                env.traceln("[AnnotationParser.scanAnnotationData]:: Enum) keyword: " + scanner.stringValue);
                data = scanAnnotationEnum(name);
                break;
            case IDENT:
                env.traceln("[AnnotationParser.scanAnnotationData]:: JASM Keyword: (annotation field name: " + name + ") keyword: " + scanner.stringValue);
                data = scanAnnotationIdent(scanner.stringValue, name);
                break;
            case ANNOTATION:
                env.traceln("[AnnotationParser.scanAnnotationData]:: Annotation Field: " + name + " = " + scanner.stringValue);
                data = new AnnotationElemValue(parseAnnotation());
                break;
            case LBRACE:
                env.traceln("[AnnotationParser.scanAnnotationData]:: Annotation Array Field: " + name);
                data = scanAnnotationArray(name);
                break;
            default:
                env.error(scanner.pos, "incorrect.annot.token", scanner.token);
                throw new Scanner.SyntaxError();
        }

        return data;
    }

    /**
     * scanAnnotationIdent
     *
     * parses the identifier of an annotation.
     *
     * @param ident Basic Type identifier
     * @param name Annotation Name
     * @return Basic Type Annotation data
     * @throws IOException if scanning errors occur
     */
    private Data scanAnnotationIdent(String ident, String name) throws IOException {
        // Handle JASM annotation Keyword Identifiers
        Data data = null;
        BasicType type = basictype(ident);
        switch (type) {

            case T_BOOLEAN:
                // consume the keyword, get the value
                scanner.scan();
                switch (scanner.token) {
                    case INTVAL:
                        // Handle Boolean value in integer form
                        env.traceln("Boolean Field: " + name + " = " + scanner.intValue);
                        Integer val = new Integer(scanner.intValue);
                        if (val > 1 || val < 0) {
                            env.traceln("Warning: Boolean Field: " + name + " value is not 0 or 1, value = " + scanner.intValue);
                        }
                        data = new ConstElemValue('Z', parser.pool.FindCell(ConstType.CONSTANT_INTEGER, val));
                        scanner.scan();
                        break;
                    case IDENT:
                        // handle boolean value with true/false keywords
                        int val1 = 0;
                        switch (scanner.stringValue) {
                            case "true":
                                val1 = 1;
                                break;
                            case "false":
                                val1 = 0;
                                break;
                            default:
                                throw new IOException("Incorrect Annotation (boolean), expected true/false), got \"" + scanner.stringValue + "\".");
                        }
                        env.traceln("Boolean Field: " + name + " = " + scanner.stringValue);
                        data = new ConstElemValue('Z', parser.pool.FindCell(ConstType.CONSTANT_INTEGER, new Integer(val1)));
                        scanner.scan();
                        break;
                    default:
                        env.error(scanner.pos, "incorrect.annot.bool", scanner.stringValue);
                        throw new Scanner.SyntaxError();
                }
                break;
            case T_BYTE:
                // consume the keyword, get the value
                scanner.scan();
                switch (scanner.token) {
                    case INTVAL:
                        env.traceln("Byte Field: " + name + " = " + scanner.intValue);
                        Integer val = new Integer(scanner.intValue);
                        if (val > 0xFF) {
                            env.traceln("Warning: Byte Field: " + name + " value is greater than 0xFF, value = " + scanner.intValue);
                        }
                        data = new ConstElemValue('B', parser.pool.FindCell(ConstType.CONSTANT_INTEGER, val));
                        scanner.scan();
                        break;
                    default:
                        env.error(scanner.pos, "incorrect.annot.byte", scanner.stringValue);
                        throw new Scanner.SyntaxError();
                }
                break;
            case T_CHAR:
                // consume the keyword, get the value
                scanner.scan();
                switch (scanner.token) {
                    case INTVAL:
                        env.traceln("Char Field: " + name + " = " + scanner.intValue);
                        Integer val = new Integer(scanner.intValue);
                        // Bounds check?
                        data = new ConstElemValue('C', parser.pool.FindCell(ConstType.CONSTANT_INTEGER, val));
                        scanner.scan();
                        break;
                    default:
                        env.error(scanner.pos, "incorrect.annot.char", scanner.stringValue);
                        throw new Scanner.SyntaxError();
                }
                break;
            case T_SHORT:
                // consume the keyword, get the value
                scanner.scan();
                switch (scanner.token) {
                    case INTVAL:
                        env.traceln("Short Field: " + name + " = " + scanner.intValue);
                        Integer val = new Integer(scanner.intValue);
                        if (val > 0xFFFF) {
                            env.traceln("Warning: Short Field: " + name + " value is greater than 0xFFFF, value = " + scanner.intValue);
                        }
                        data = new ConstElemValue('S', parser.pool.FindCell(ConstType.CONSTANT_INTEGER, val));
                        scanner.scan();
                        break;
                    default:
                        env.error(scanner.pos, "incorrect.annot.short", scanner.stringValue);
                        throw new Scanner.SyntaxError();
                }
                break;
            default:
                env.error(scanner.pos, "incorrect.annot.keyword", ident);
                throw new Scanner.SyntaxError();
        }

        return data;
    }
}
