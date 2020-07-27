package io.github.bigbio.pgatk.io.msp;

import io.github.bigbio.pgatk.io.common.PgatkIOException;
import io.github.bigbio.pgatk.io.utils.AminoAcid;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MspAttributeReader {

  private static final int C_TERM = -2;
  private static final int N_TERM = -1;

  static List<Character> aaList;

  static {
    aaList = new ArrayList<>();
    for(AminoAcid aa : AminoAcid.values()) {
      aaList.add(aa.getSymbol().charAt(0));
    }
  }


  public static void parseName(String toString, LibrarySpectrumBuilder builder) throws PgatkIOException {
    String[] nameValues = toString.split("/");
    parsePeptideFromPeptidoform(nameValues[0], builder);
    builder.setCharge(Integer.parseInt(nameValues[1]));
  }

  public static boolean parseComment(String comment,LibrarySpectrumBuilder builder) {

    boolean handled = false;

    int length = comment.length();
    boolean inQuotes = false;
    int last = 0;
    for(int i = 0; i < length; i++){

      char current = comment.charAt(i);

      if(!inQuotes && current == ' ') {

        try {
          handled = handled | handle(comment.substring(last, i), builder);
        } catch (Exception e) {

          throw new IllegalStateException("Comment [" + comment + "] is not a valid comment.", e);
        }
        last = i + 1;
      } else if(current == '"') {

        inQuotes = !inQuotes;
      }
    }

    if(inQuotes)
      throw new IllegalStateException("Unclosed Quotes in : " + comment);
    try {
      handled = handled | handle(comment.substring(last, length), builder);
    } catch (Exception e) {

      throw new IllegalStateException("Comment [" + comment + "] is not a valid comment.", e);
    }

    return handled;
  }

  private static boolean handle(String split, LibrarySpectrumBuilder builder) {

    int index = split.indexOf('=');
    if(index == -1) {
      throw new IllegalStateException("Split [" + split + "] does not contain =");
    }

    boolean handled = false;
    String tag = split.substring(0, index);
    String value = split.substring(index + 1);
    if(tag.equals("Mods")) {
      handled = parseModsComment(value, builder);
    } else if(tag.equals("Protein")) {
      handled = parseProteinComment(value, builder);
    } else if(tag.equals("Parent")){
      builder.setPrecursorMz(Double.parseDouble(value.trim()));
    } else if(tag.equals("Dotbest") || tag.equals("Probcorr")){
      builder.addScores(tag, value);
    }else {
      handled = parseUnknownCommentTag(tag, value, builder);
    }

    return handled;
  }

  protected static boolean parseModsComment(String value, LibrarySpectrumBuilder builder) {

    String[] split = value.split("/");

    for(int i = 1; i < split.length; i++) { //starting at 1 because split 0 is the mod count

      String[] modSplit = split[i].split(",");

      int index = Integer.parseInt(modSplit[0]);

      if(index == -1)
        builder.addMod(N_TERM, modSplit[2]);
      else if(index == -2)
        builder.addMod(C_TERM, modSplit[2]);
      else
        builder.addMod(index, modSplit[2]);
    }

    return false;
  }

  protected static boolean parseProteinComment(String value, LibrarySpectrumBuilder builder) {
    value = value.replaceAll("\"","");
    builder.addProteinAccessionNumber(value);
    return false;
  }

  protected static boolean parseUnknownCommentTag(String tag, String value, LibrarySpectrumBuilder builder) {
    return false;
  }

  public static void parsePeptideFromPeptidoform(String s, LibrarySpectrumBuilder builder) throws PgatkIOException {

    String input = s;
    int bracketCount = 0;
    int bracketStartIndex = -1;
    int lastModIndex = -1;
    List<Character> aaChars = new ArrayList<>();
    for(int i = 0; i < s.length(); i++) {

      char c = s.charAt(i);

      if(bracketCount == 0 && aaList.contains(c)) {
        aaChars.add(c);
      } else if(c == '(') {
        if(bracketCount == 0) bracketStartIndex = i;
        bracketCount += 1;
      } else if(c == ')') {

        bracketCount -= 1;
        if(bracketCount == 0) {
          if(lastModIndex == aaChars.size() - 1)  //Prevent A(mod1)(mod2) from being legal
            throw new PgatkIOException(input + " has illegal modification format");
          System.out.println("PTM -- ( " + s.substring(bracketStartIndex + 1, i) + " )");
          lastModIndex = aaChars.size() - 1;
        }
      } else if(bracketCount == 0) {
        throw new PgatkIOException(input + " has illegal character " + c);
      }

      if(bracketCount < 0) throw new PgatkIOException(input + " has to many closing brackets" + i);
    }

    if(bracketCount != 0)
      throw new PgatkIOException(input + " has to many opening brackets");

    if(s.isEmpty())
      throw new PgatkIOException("The peptide '" + input + "' contains no amino acid residues");

    builder.setPeptideSequence(aaChars.stream().map(String::valueOf).collect(Collectors.joining()));
  }

//  private void parseMods(String substring, LibrarySpectrumBuilder builder) {
//
//    for(String sMod : substring.split(",\\s?")) {
//
//      Optional<Modification> optional = modResolver.resolve(sMod);
//      if (optional.isPresent()) {
//
//        termModMap.put(modAttachment, optional.get());
//      } else {
//
//        throw new PeptideParseException("Cannot resolve modification " + sMod);
//      }
//    }
//  }


}
