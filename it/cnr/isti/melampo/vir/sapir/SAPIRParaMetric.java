package it.cnr.isti.melampo.vir.sapir;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.mpeg7.vd.ColorLayout;
import it.cnr.isti.vir.features.mpeg7.vd.ColorStructure;
import it.cnr.isti.vir.features.mpeg7.vd.EdgeHistogram;
import it.cnr.isti.vir.features.mpeg7.vd.HomogeneousTexture;
import it.cnr.isti.vir.features.mpeg7.vd.ScalableColor;
import it.cnr.isti.vir.similarity.metric.Metric;

public class SAPIRParaMetric implements Metric<IFeaturesCollector> {

	
	private static long distCount = 0;
	public static final FeatureClassCollector reqFeatures = new FeatureClassCollector(
			ColorLayout.class,
			ColorStructure.class,
			ScalableColor.class,
			EdgeHistogram.class,
			HomogeneousTexture.class );
	
	protected static final int htOption = HomogeneousTexture.N_OPTION;
	
	public final long getDistCount() {
		return distCount;
	}

	public double[] wSAPIR = {
		1.5  * 1.0/300.0,  //CL
		2.5  * 1.0/10200.0, //CS
		4.5  * 1.0/68.0, //EH
		0.5  * 1.0/25.0,  //HT
		2.5  * 1.0/3000.0  //SC
	};
	
	public SAPIRParaMetric(double clw, double csw, double ehw, double htw, double scw){
		wSAPIR[0] = wSAPIR[0]*clw;
		wSAPIR[1] = wSAPIR[1]*csw;
		wSAPIR[2] = wSAPIR[2]*ehw;
		wSAPIR[3] = wSAPIR[3]*htw;
		wSAPIR[4] = wSAPIR[4]*scw;		
	}
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
/*	public final double distance(Image img1, Image img2) {
		return distance(img1.getFeatures(), img2.getFeatures());
	}*/
	
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2 ) {
	
		return distance(f1,f2, Double.MAX_VALUE);
	}	
	
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2, double max ) {
		distCount++;
		double dist = 0;

		//EH
		dist += wSAPIR[2] * EdgeHistogram.mpeg7XMDistance( (EdgeHistogram) f1.getFeature(EdgeHistogram.class), (EdgeHistogram) f2.getFeature(EdgeHistogram.class) );
		if ( dist > max ) return -dist;
		
		//CS
		dist += wSAPIR[1] * ColorStructure.mpeg7XMDistance( (ColorStructure) f1.getFeature(ColorStructure.class), (ColorStructure) f2.getFeature(ColorStructure.class) );
		if ( dist > max ) return -dist;		
		
		//SC
		dist += wSAPIR[4] * ScalableColor.mpeg7XMDistance( (ScalableColor) f1.getFeature(ScalableColor.class), (ScalableColor) f2.getFeature(ScalableColor.class) );
		if ( dist > max ) return -dist;	
		
		//CL
		dist += wSAPIR[0] * ColorLayout.mpeg7XMDistance( (ColorLayout) f1.getFeature(ColorLayout.class), (ColorLayout) f2.getFeature(ColorLayout.class) );
		if ( dist > max ) return -dist;	
		
		//HT
		dist += wSAPIR[3] * HomogeneousTexture.mpeg7XMDistance( (HomogeneousTexture) f1.getFeature(HomogeneousTexture.class), (HomogeneousTexture) f2.getFeature(HomogeneousTexture.class), htOption );
				
		return dist;
	}
	
	public String toString() {
		return this.getClass().toString();
	}
	
}
