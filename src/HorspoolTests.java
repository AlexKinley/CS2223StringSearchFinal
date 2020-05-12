import static org.junit.Assert.assertEquals;

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
}
