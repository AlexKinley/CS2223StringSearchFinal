import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class HorspoolTests {

	HorspoolSearch hS;

	@Test
	public void testSearchOne(){
		hS = new HorspoolSearch("Mary had a little lamb");
		assertEquals(0, hS.search("Mary"));
		assertEquals(-1, hS.search("mary"));
		
		assertEquals(18, hS.search("lamb"));
		assertEquals(17, hS.search(" lamb"));
		
		assertEquals(5, hS.search("had a "));
	}
	
	@Test
	public void testSearchTwo(){
		hS = new HorspoolSearch("JIM SAW ME IN THE BARBERSHOP");
		assertEquals(18, hS.search("BARBER"));
		
		assertEquals(-1, hS.search("SHOPER"));
		assertEquals(1, hS.search("IM SAW"));
	}
	
	@Test
	public void testSearchCaseInsensitiveOne(){
		hS = new HorspoolSearch("Mary had a little lamb");
		assertEquals(0, hS.searchCaseInsensitive("Mary"));
		assertEquals(0, hS.searchCaseInsensitive("mary"));
		
		assertEquals(18, hS.searchCaseInsensitive("lamb"));
		assertEquals(18, hS.searchCaseInsensitive("LAMB"));
		assertEquals(17, hS.searchCaseInsensitive(" lamb"));
		
		assertEquals(5, hS.searchCaseInsensitive("had a "));
	}
	
	@Test
	public void testSearchCaseInsensitiveTwo(){
		hS = new HorspoolSearch("JIM SAW ME IN THE BARBERSHOP");
		assertEquals(18, hS.searchCaseInsensitive("BARBER"));
		assertEquals(18, hS.searchCaseInsensitive("barber"));
		
		assertEquals(-1, hS.searchCaseInsensitive("SHOPER"));
		assertEquals(1, hS.searchCaseInsensitive("IM SAW"));
		assertEquals(1, hS.searchCaseInsensitive("im saw"));
	}
	
	@Test
	public void testFindAllCaseInsensitiveOne(){
		hS = new HorspoolSearch("the blue man lives in a blue house with his blue dog");
		List<Integer> indexes;
		indexes = hS.findAllCaseInsensitive("blue");
		assertEquals(3, indexes.size());
		indexes = hS.findAllCaseInsensitive("BLUE");
		assertEquals(3, indexes.size());
		
		indexes = hS.findAllCaseInsensitive("man");
		assertEquals(1, indexes.size());
		indexes = hS.findAllCaseInsensitive("MAN");
		assertEquals(1, indexes.size());
		
		indexes = hS.findAllCaseInsensitive("red");
		assertEquals(0, indexes.size());
		indexes = hS.findAllCaseInsensitive("RED");
		assertEquals(0, indexes.size());
	}
	
	@Test
	public void testFindAllCaseInsensitiveTwo(){
		hS = new HorspoolSearch("BANANANANA");
		List<Integer> indexes;
		indexes = hS.findAllCaseInsensitive("BANANA");
		assertEquals(1, indexes.size());
		indexes = hS.findAllCaseInsensitive("banana");
		assertEquals(1, indexes.size());
		
		indexes = hS.findAllCaseInsensitive("AN");
		assertEquals(4, indexes.size());
		indexes = hS.findAllCaseInsensitive("an");
		assertEquals(4, indexes.size());
		
		indexes = hS.findAllCaseInsensitive("ANA");
		assertEquals(4, indexes.size());
		indexes = hS.findAllCaseInsensitive("ana");
		assertEquals(4, indexes.size());
	}
	
	@Test
	public void testFindAllOne(){
		hS = new HorspoolSearch("the blue man lives in a blue house with his blue dog");
		List<Integer> indexes;
		indexes = hS.findAll("blue");
		assertEquals(3, indexes.size());
		
		indexes = hS.findAll("man");
		assertEquals(1, indexes.size());
		
		indexes = hS.findAll("red");
		assertEquals(0, indexes.size());
	}
	
	@Test
	public void testFindAllTwo(){
		hS = new HorspoolSearch("BANANANANA");
		List<Integer> indexes;
		indexes = hS.findAll("BANANA");
		assertEquals(1, indexes.size());
		
		indexes = hS.findAll("AN");
		assertEquals(4, indexes.size());
		
		indexes = hS.findAll("ANA");
		assertEquals(4, indexes.size());
	}
}
