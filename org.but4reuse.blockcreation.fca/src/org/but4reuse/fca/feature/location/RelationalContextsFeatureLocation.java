package org.but4reuse.fca.feature.location;

import java.util.ArrayList;
import java.util.List;

import org.but4reuse.adaptedmodel.AdaptedModel;
import org.but4reuse.adaptedmodel.Block;
import org.but4reuse.adaptedmodel.helpers.AdaptedModelHelper;
import org.but4reuse.fca.utils.FCAUtils;
import org.but4reuse.feature.location.IFeatureLocation;
import org.but4reuse.feature.location.LocatedFeature;
import org.but4reuse.featurelist.Feature;
import org.but4reuse.featurelist.FeatureList;
import org.but4reuse.featurelist.helpers.FeatureListHelper;
import org.eclipse.core.runtime.IProgressMonitor;

import com.googlecode.erca.Attribute;
import com.googlecode.erca.clf.Concept;
import com.googlecode.erca.clf.ConceptLattice;
import com.googlecode.erca.rcf.FormalContext;

/**
 * Relational contexts feature location
 * 
 * @author jabier.martinez
 * 
 */
public class RelationalContextsFeatureLocation implements IFeatureLocation {

	@Override
	public List<LocatedFeature> locateFeatures(FeatureList featureList, AdaptedModel adaptedModel,
			IProgressMonitor monitor) {
		List<LocatedFeature> locatedFeatures = new ArrayList<LocatedFeature>();

		FormalContext artefactsFeaturesAndBlocks = FCAUtils.createArtefactsFeaturesAndBlocksFormalContext(featureList,
				adaptedModel);
		ConceptLattice cl = FCAUtils.createConceptLattice(artefactsFeaturesAndBlocks);

		for (Concept concept : cl.getConcepts()) {
			if (!concept.getSimplifiedIntent().isEmpty()) {
				List<Block> blocks = getBlocksOfConcept(adaptedModel, concept);
				List<Feature> features = getFeaturesOfConcept(featureList, concept);
				for (Feature f : features) {
					for (Block b : blocks) {
						LocatedFeature locatedFeature = new LocatedFeature(f, b, 1);
						locatedFeatures.add(locatedFeature);
					}
				}
			}
		}
		return locatedFeatures;
	}

	private List<Feature> getFeaturesOfConcept(FeatureList featureList, Concept concept) {
		List<Feature> features = new ArrayList<Feature>();
		for (Attribute a : concept.getSimplifiedIntent()) {
			if (a.getDescription().startsWith("F: ")) {
				features.add(FeatureListHelper.getFeatureByName(featureList,
						a.getDescription().substring("F: ".length())));
			}
		}
		return features;
	}

	private List<Block> getBlocksOfConcept(AdaptedModel adaptedModel, Concept concept) {
		List<Block> blocks = new ArrayList<Block>();
		for (Attribute a : concept.getSimplifiedIntent()) {
			if (a.getDescription().startsWith("B: ")) {
				blocks.add(AdaptedModelHelper.getBlockByName(adaptedModel, a.getDescription().substring("B: ".length())));
			}
		}
		return blocks;
	}

}
