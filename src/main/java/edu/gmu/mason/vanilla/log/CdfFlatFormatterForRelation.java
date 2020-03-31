package edu.gmu.mason.vanilla.log;

import java.util.Collection;
import java.util.Iterator;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 * General description_________________________________________________________
 * A class used for CDF formatting
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class CdfFlatFormatterForRelation extends AbstractFormatter {
	private static final long serialVersionUID = 4871711945409433387L;

	@Override
	protected String format(Object value) {
		// we assume have graph data
		StringBuilder sb = new StringBuilder();
		if (value instanceof Graph) {
			Graph graph = (Graph) value;
			Collection<Node> fromNodes = graph.getNodeSet();
			Iterator<Node> iter = null;
			Node toNode = null;
			for (Node fromNode : fromNodes) {
				while (true) {
					if (iter == null) {
						iter = fromNode.getNeighborNodeIterator();
					}

					if (iter.hasNext()) {
						sb.append((long) graph.getStep()).append("\t");
						sb.append("FriendFamily").append("\t");
						sb.append("yes").append("\t");
						sb.append(fromNode.getId()).append("\t");
						toNode = iter.next();
						sb.append(toNode.getId()).append("\t");
						sb.append("1").append("\t");
						sb.append("\t");
						sb.append("\n");
					} else {
						iter = null;
						break;
					}
				}
			}
		}
		return sb.toString();
	}

}
