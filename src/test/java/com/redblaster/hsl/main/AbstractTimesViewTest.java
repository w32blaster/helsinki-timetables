package com.redblaster.hsl.main;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.redblaster.hsl.main.AbstractTimesView.Cell;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.redblaster.hsl.main.AbstractTimesView.groupByHours;
import static org.junit.Assert.*;

/**
 * Created by w32blaster on 06/11/17.
 */
public class AbstractTimesViewTest {

    @Test
    public void testGroupByHoursEmptyCells() throws Exception {

        // Given:
        List<Cell> cells = ImmutableList.of();

        // When:
        final Object[] results = groupByHours(cells);

        // Then
        assertNotNull(results);
        assertEquals(2, results.length);

        // and:
        Map<Integer, List<Cell>> mapOfCells = (Map<Integer, List<Cell>>) results[0];
        int maxMins = (int) results[1];

        assertTrue(mapOfCells.isEmpty());
        assertEquals(0, maxMins);
    }

    @Test
    public void testGroupByHoursOneHour() {

        // Given:
        List<Cell> cells = ImmutableList.of(
                new Cell(null, "1803"),
                new Cell(null, "1816"),
                new Cell(null, "1826"),
                new Cell(null, "1845")
        );

        // When:
        final Object[] results = groupByHours(cells);

        // Then
        assertNotNull(results);
        assertEquals(2, results.length);

        // and:
        Map<Integer, List<Cell>> mapOfCells = (Map<Integer, List<Cell>>) results[0];
        int maxMins = (int) results[1];

        // and:
        assertFalse(mapOfCells.isEmpty());
        assertEquals(1, mapOfCells.size());
        assertEquals("1803", mapOfCells.get(18).get(0).time);
        assertEquals("1816", mapOfCells.get(18).get(1).time);
        assertEquals("1826", mapOfCells.get(18).get(2).time);
        assertEquals("1845", mapOfCells.get(18).get(3).time);

        // and:
        assertEquals(4, maxMins);
    }

    @Test
    public void testGroupByHoursFewHours() {

        // Given:
        List<Cell> cells = ImmutableList.of(
                new Cell(null, "1757"),
                new Cell(null, "1803"),
                new Cell(null, "1816"),
                new Cell(null, "1826"),
                new Cell(null, "1845"),
                new Cell(null, "1915"),
                new Cell(null, "1938")
        );

        // When:
        final Object[] results = groupByHours(cells);

        // Then
        assertNotNull(results);
        assertEquals(2, results.length);

        // and:
        Map<Integer, List<Cell>> mapOfCells = (Map<Integer, List<Cell>>) results[0];
        int maxMins = (int) results[1];

        // and:
        assertFalse(mapOfCells.isEmpty());
        assertEquals(3, mapOfCells.size());

        assertEquals("1757", mapOfCells.get(17).get(0).time);

        assertEquals("1803", mapOfCells.get(18).get(0).time);
        assertEquals("1816", mapOfCells.get(18).get(1).time);
        assertEquals("1826", mapOfCells.get(18).get(2).time);
        assertEquals("1845", mapOfCells.get(18).get(3).time);

        assertEquals("1915", mapOfCells.get(19).get(0).time);
        assertEquals("1938", mapOfCells.get(19).get(1).time);

        // and:
        assertEquals(4, maxMins);
    }
}