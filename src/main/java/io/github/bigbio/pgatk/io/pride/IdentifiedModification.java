package io.github.bigbio.pgatk.io.pride;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.bigbio.pgatk.io.utils.Tuple;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ypriverol
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdentifiedModification{


    private CvParam neutralLoss;
    private List<Tuple<Integer, Set<CvParam>>> positionMap;
    private CvParam modification;
    private Set<? extends  CvParam> attributes;

    /**
     * Default constructor
     */
    public IdentifiedModification() { }

    /**
     * Constructor with all parameters
     *  @param neutralLoss neutral loss {@link CvParam}
     * @param positionMap Map Position and List of {@link CvParam}
     * @param modification modification as {@link CvParam}
     * @param attributes Attributes
     */
    public IdentifiedModification(CvParam neutralLoss, List<Tuple<Integer, Set<CvParam>>>
            positionMap, CvParam modification, Set<CvParam> attributes) {
        this.neutralLoss = neutralLoss;
        this.positionMap = positionMap;
        this.modification = modification;
        this.attributes = attributes;
    }

    public void setNeutralLoss(CvParam neutralLoss) {
        this.neutralLoss = neutralLoss;
    }

    public void setPositionMap(List<Tuple<Integer, Set<CvParam>>> positionMap) {
        this.positionMap = positionMap;
    }

    public void setModification(CvParam modification) {
        this.modification = modification;
    }

    public void setAttributes(Set<? extends  CvParam> attributes) {
        this.attributes = attributes;
    }

    public CvParam getNeutralLoss() {
        return this.neutralLoss;
    }

    public List<Tuple<Integer, Set<CvParam>>> getPositionMap() {
        return this.positionMap;
    }

    @JsonIgnore
    public CvParam getModificationCvTerm() {
        return modification;
    }

    @JsonIgnore
    public Collection<? extends String> getAdditionalAttributesStrings() {
        List<String> attributes = Collections.emptyList();
        if(this.attributes != null && !this.attributes.isEmpty())
            attributes = this.attributes.stream().map(CvParam::getName).collect(Collectors.toList());
        return attributes;
    }

    public CvParam getModification() {
        return modification;
    }

    public Set<? extends  CvParam> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "IdentifiedModification{" +
                "neutralLoss=" + neutralLoss +
                ", positionMap=" + positionMap +
                ", modification=" + modification +
                ", attributes=" + attributes +
                '}';
    }

    public void addPosition(int proteinPosition, Set<CvParam> score) {
        if(positionMap == null)
            positionMap = new ArrayList<Tuple<Integer, Set<CvParam>>>();
        Tuple<Integer, Set<CvParam>> scoreTuple = new Tuple<Integer, Set<CvParam>>(proteinPosition, score);
        positionMap.add(scoreTuple);
    }
}
