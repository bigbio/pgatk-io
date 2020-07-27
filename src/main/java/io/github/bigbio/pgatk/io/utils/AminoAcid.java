/**
 * Copyright (c) 2010, SIB. All rights reserved.
 *
 * SIB (Swiss Institute of Bioinformatics) - http://www.isb-sib.ch Host -
 * http://mzjava.expasy.org
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of the SIB/GENEBIO nor the names of
 * its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL SIB/GENEBIO BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.github.bigbio.pgatk.io.utils;


public enum AminoAcid {

    A("C3H5NO"), R("C6H12N4O"), N("C4H6N2O2"), D("C4H5NO3"), C("C3H5NOS"), E("C5H7NO3"), Q("C5H8N2O2"), G("C2H3NO"), H("C6H7N3O"), K("C6H12N2O"),
    M("C5H9NOS"), F("C9H9NO"), P("C5H7NO"), S("C3H5NO2"), T("C4H7NO2"), W("C11H10N2O"), Y("C9H9NO2"), V("C5H9NO"), J("C6H11NO"), I("C6H11NO"),
    L("C6H11NO"), O("C12H19O2N3"), U("C3H5ONSe"), X(), B(), Z();

    private final String composition;

    private AminoAcid(){

        composition = null;
    }

    private AminoAcid(String comp) {

        this.composition = comp;
    }

    public String getSymbol() {
        return toString();
    }


    public static AminoAcid valueOf(char aa) {

        switch (aa) {

            case 'A':
                return A;
            case 'R':
                return R;
            case 'N':
                return N;
            case 'D':
                return D;
            case 'C':
                return C;
            case 'E':
                return E;
            case 'Q':
                return Q;
            case 'G':
                return G;
            case 'H':
                return H;
            case 'K':
                return K;
            case 'M':
                return M;
            case 'F':
                return F;
            case 'P':
                return P;
            case 'S':
                return S;
            case 'T':
                return T;
            case 'W':
                return W;
            case 'Y':
                return Y;
            case 'V':
                return V;
            case 'J':
                return J;
            case 'I':
                return I;
            case 'L':
                return L;
            case 'O':
                return O;
            case 'U':
                return U;
            case 'X':
                return X;
            case 'B':
                return B;
            case 'Z':
                return Z;
            default:

                String symbol = Character.toString(aa).toUpperCase();
                return AminoAcid.valueOf(symbol);
        }
    }



    /**
     * Returns true if this amino acid symbol unambiguously refers to only one amino acid.
     *
     * @return true if this amino acid symbol unambiguously refers to only one amino acid.
     */
    public boolean isUnambiguous(){

        return composition != null;
    }
}
