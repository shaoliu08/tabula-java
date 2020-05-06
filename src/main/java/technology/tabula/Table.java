package technology.tabula;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import technology.tabula.extractors.ExtractionAlgorithm;

@SuppressWarnings("serial")
public class Table extends Rectangle {
	private final String extractionMethod;

	private int rowCount = 0;
	private int colCount = 0;
	// memoizedRows is a cache.
	private List<List<RectangularTextContainer>> memoizedRows = null;
	/* visible for testing */
	final TreeMap<CellPosition, RectangularTextContainer> cells = new TreeMap<>();

	public static final Table empty() {
		return new Table("");
	}

	private Table(String extractionMethod) {
		this.extractionMethod = extractionMethod;
	}

	public Table(ExtractionAlgorithm extractionAlgorithm) {
		this(extractionAlgorithm.toString());
	}

	public int getRowCount() { return rowCount; }
	public int getColCount() { return colCount; }

	public String getExtractionMethod() { return extractionMethod; }

	public void add(RectangularTextContainer chunk, int row, int col) {
		this.merge(chunk);	// union the rectangle of new cell with the table
		
		rowCount = Math.max(rowCount, row + 1);
		colCount = Math.max(colCount, col + 1);
		
		CellPosition cp = new CellPosition(row, col);
		
		RectangularTextContainer old = cells.get(cp);
		if (old != null) chunk.merge(old);	// union the rectangle of new cell with the existing cell
		cells.put(cp, chunk);  // TODO: question? how about the content in the existing cell?

		this.memoizedRows = null;
	}

	public List<List<RectangularTextContainer>> getRows() {
		if (this.memoizedRows == null) this.memoizedRows = computeRows();
		return this.memoizedRows;
	}

	/**
	 * compute all cells in this table
	 * @return
	 */
	private List<List<RectangularTextContainer>> computeRows() {
		List<List<RectangularTextContainer>> rows = new ArrayList<>();
		for (int i = 0; i < rowCount; i++) {
			List<RectangularTextContainer> lastRow = new ArrayList<>();
			rows.add(lastRow);
			for (int j = 0; j < colCount; j++) {
				lastRow.add(getCell(i, j));
			}
		}
		return rows;
	}
	
	public RectangularTextContainer getCell(int i, int j) {
		RectangularTextContainer cell = cells.get(new CellPosition(i,j)); // JAVA_8 use getOrDefault()
		return cell != null ? cell : TextChunk.EMPTY;
	}

}

/**
 * CellPosition
 */
class CellPosition implements Comparable<CellPosition> {

	CellPosition(int row, int col) {
		this.row = row;
		this.col = col;
	}

	final int row, col;

	@Override public int hashCode() {
		return row + 101 * col;
	}

	@Override public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		CellPosition other = (CellPosition) obj;
		return row == other.row && col == other.col;
	}

	@Override public int compareTo(CellPosition other) {
		int rowdiff = row - other.row;
		return rowdiff != 0 ? rowdiff : col - other.col;
	}

}
