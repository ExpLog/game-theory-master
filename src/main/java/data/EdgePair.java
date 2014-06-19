package data;

public class EdgePair {
	private int sourceId, sinkId;
	public EdgePair(int sourceId, int sinkId) {
		this.sourceId = sourceId;
		this.sinkId = sinkId;
	}
	public int getSourceId(){
		return sourceId;
	}
	public int getSinkId() {
		return sinkId;
	}

    @Override
	public int hashCode() {
		return new Integer(sourceId).hashCode() + new Integer(sinkId).hashCode()*13;
	}
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof EdgePair)) {
			return false;
		}
		EdgePair p = (EdgePair)o;
		return p.sourceId == sourceId && p.sinkId == sinkId;
	}
}
