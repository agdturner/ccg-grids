/*
 * Copyright 2019 Andy Turner, University of Leeds.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.leeds.ccg.grids.d2.stats;

import ch.obermuhlner.math.big.BigRational;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.TreeMap;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;

/**
 * To be extended to provide statistics about the data in Grids and GridChunks
 * more optimally.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_StatsNumber extends Grids_Stats {

    private static final long serialVersionUID = 1L;

    /**
     * For storing the sum of all non data values.
     */
    protected BigRational sum;

    /**
     * For storing the number of minimum data values.
     */
    protected long nMin;

    /**
     * For storing the number of maximum data values.
     */
    protected long nMax;

    /**
     * Create a new instance.
     * 
     * @param ge Grids_Environment
     */
    public Grids_StatsNumber(Grids_Environment ge) {
        super(ge);
        init0();
    }

    /**
     * Initialises the statistics by setting sum, nMin and nMax equal to 0.
     */
    private void init0() {
        sum = BigRational.ZERO;
        nMin = 0;
        nMax = 0;
    }
    
    @Override
    protected void init() {
        super.init();
        init0();
    }

    /**
     * Updates from stats.
     *
     * @param stats the Grids_StatsNumber instance which fields are used to
     * update this.
     */
    public void update(Grids_StatsNumber stats) {
        super.update(stats);
        sum = stats.sum;
        nMin = stats.nMin;
        nMax = stats.nMax;
    }

    /**
     * Override to provide a more detailed fields description.
     *
     * @return A text description of this.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     * @throws java.lang.Exception If encountered.
     */
    @Override
    public String getFieldsDescription() throws IOException, Exception, 
            ClassNotFoundException {
        return super.getFieldsDescription()
                + ", Max=" + getMax(false) + ", Min=" + getMin(false)
                + ", NMax=" + nMax + ", NMin=" + nMin + ", Sum=" + sum;
    }

    /**
     * @return The number of cells with finite non zero data values.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     * @throws java.lang.Exception If encountered.
     */
    public abstract long getNonZeroN() throws IOException, Exception,
            ClassNotFoundException;

    /**
     * Get the minimum of all data values.
     *
     * @param update If true then update() is called.
     * @return The minimum of all data values.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     * @throws java.lang.Exception If encountered.
     */
    public abstract Number getMin(boolean update) throws IOException, Exception,
            ClassNotFoundException;

    /**
     * Get the maximum of all data values.
     *
     * @param update If true then an update of the statistics is made.
     * @return The maximum of all data values.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     * @throws java.lang.Exception If encountered.
     */
    public abstract Number getMax(boolean update) throws IOException, Exception,
            ClassNotFoundException;

    /**
     * Get the arithmetic mean of all data values. Throws an ArithmeticException
     * if {@link #n} is equal to zero.
     *
     * @return The arithmetic mean of all data values.
     */
    public BigRational getArithmeticMean() {
        return sum.divide(BigInteger.valueOf(n));
    }

    /**
     * For getting a quantile class map.
     * @param nClasses nClasses
     * @return Object[]
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     * @throws ClassNotFoundException If encountered.
     */
    public abstract Object[] getQuantileClassMap(int nClasses)
            throws IOException, Exception, ClassNotFoundException;

    /**
     * @param v Value.
     * @param classMap Class map.
     * @param mins Minimums.
     * @param maxs Maximums.
     * @param classCounts Class counts.
     * @param dnvpc The desired number of values per class.
     * @param classToFill Class to fill.
     * @return result[0] is the class, result[1] is the classToFill which may or
     * may not change from what is passed in.
     */
    protected int[] getValueClass(BigDecimal v,
            TreeMap<Integer, TreeMap<BigDecimal, Long>> classMap,
            TreeMap<Integer, BigDecimal> mins, 
            TreeMap<Integer, BigDecimal> maxs,
            TreeMap<Integer, Long> classCounts,
            long dnvpc, int classToFill) {
        int[] r = new int[2];
        long classToFillCount = classCounts.get(classToFill);
        BigDecimal maxValueOfClassToFill = maxs.get(classToFill);
//        if (maxDouble.get(classToFill) != null) {
//            maxValueOfClassToFill = maxDouble.get(classToFill);
//        } else {
//            maxValueOfClassToFill = Double.NEGATIVE_INFINITY;
//        }
        // Special cases
        // Case 1:
        if (v.compareTo(maxValueOfClassToFill) == 1) {
            maxs.put(classToFill, v);
            classToFillCount += 1;
            addToMapCounts(v, classToFill, classMap);
            addToCount(classToFill, classCounts);
            r[0] = classToFill;
            //if (classToFillCount >= desiredNumberOfValuesInEachClass) {
            r[1] = checkClassToFillAndPropagation(r, classToFill,
                    classToFillCount, classMap, mins, maxs,
                    classCounts, dnvpc, classToFill);
//            } else {
//                result[1] = classToFill;
//            }
            return r;
        }
        // Case 2:
        if (v == maxValueOfClassToFill) {
            classToFillCount += 1;
            addToMapCounts(v, classToFill, classMap);
            addToCount(classToFill, classCounts);
            r[0] = classToFill;
            //if (classToFillCount >= desiredNumberOfValuesInEachClass) {
            r[1] = checkClassToFillAndPropagation(r, classToFill,
                    classToFillCount, classMap, mins, maxs,
                    classCounts, dnvpc, classToFill);
//            } else {
//                result[1] = classToFill;
//            }
            return r;
        }
//        // Case 3:
//        double minValueOfClass0;
//        minValueOfClass0 = minDouble.get(0);
//        if (value < minValueOfClass0) {
//            minDouble.put(0, value);
//            long class0Count;
//            class0Count = classCounts.get(0);
//            if (class0Count < desiredNumberOfValuesInEachClass) {
//                r[0] = classToFill; // Which should be 0
//                addToMapCounts(value, classToFill, classMap);
//                addToCount(classToFill, classCounts);
//                classToFillCount += 1;
//                if (classToFillCount >= desiredNumberOfValuesInEachClass) {
//                    r[1] = classToFill + 1;
//                } else {
//                    r[1] = classToFill;
//                }
//                return r;
//            } else {
//                classToFillCount += 1;
//                r[0] = 0;
//                checkClassToFillAndPropagation(r, 0, classToFillCount, classMap,
//                        minDouble, maxDouble, classCounts,
//                        desiredNumberOfValuesInEachClass, classToFill);
//                return r;
//            }
//        }
        // General Case
        // 1. Find which class the value sits in.
        // 2. If the value already exists, add to the count, else add to the map
        // 3. Check the top of the class value counts. If by moving these up the 
        //    class would not contain enough values finish, otherwise do the following:
        //    a) move the top values up to the bottom of the next class.
        //      Modify the max value in this class
        //      Modify the min value in the next class
        //      Repeat Step 3 for the next class

        // General Case
        // 1. Find which class the value sits in.
        int classToCheck = classToFill;
        BigDecimal maxToCheck = maxs.get(classToCheck);
        BigDecimal minToCheck = mins.get(classToCheck);
        boolean foundClass = false;
        while (!foundClass) {
            if (v.compareTo(minToCheck) != -1 && v.compareTo(maxToCheck) != 1) {
                r[0] = classToCheck;
                foundClass = true;
            } else {
                classToCheck--;
                if (classToCheck < 1) {
                    if (classToCheck < 0) {
                        // This means that value is less than min value so set min.
                        mins.put(0, v);
                    }
                    r[0] = 0;
                    classToCheck = 0;
                    foundClass = true;
                } else {
                    maxToCheck = minToCheck; // This way ensures there are no gaps.
                    minToCheck = mins.get(classToCheck);
                }
            }
        }
        long classToCheckCount;
        // 2. If the value already exists, add to the count, else add to the map
        // and counts and ensure maxDouble and minDouble are correct (which has 
        // to be done first)
        maxToCheck = maxs.get(classToCheck);
        if (v.compareTo(maxToCheck) == 1) {
            maxs.put(classToCheck, v);
        }
        minToCheck = mins.get(classToCheck);
        if (v.compareTo(minToCheck) == -1) {
            mins.put(classToCheck, v);
        }
        addToMapCounts(v, classToCheck, classMap);
        addToCount(classToCheck, classCounts);
        classToCheckCount = classCounts.get(classToCheck);
        // 3. Check the top of the class value counts. If by moving these up the 
        //    class would not contain enough values finish, otherwise do the following:
        //    a) move the top values up to the bottom of the next class.
        //      Modify the max value in this class
        //      Modify the min value in the next class
        //      Repeat Step 3 for the next class
        //result[1] = checkValueCounts(
        checkClassToFillAndPropagation(r, classToCheck, classToCheckCount,
                classMap, mins, maxs, classCounts,
                dnvpc, classToFill);
        //classCounts.put(classToCheck, classToFillCount + 1);
        return r;
    }

    private void addToCount(int index, TreeMap<Integer, Long> classCounts) {
        long count = classCounts.get(index);
        count++;
        classCounts.put(index, count);
    }

    private <T> void addToMapCounts(T v, int classToCount,
            TreeMap<Integer, TreeMap<T, Long>> classMap) {
        TreeMap<T, Long> classToCheckMap;
        classToCheckMap = classMap.get(classToCount);
        if (classToCheckMap.containsKey(v)) {
            long count = classToCheckMap.get(v);
            count++;
            classToCheckMap.put(v, count);
        } else {
            classToCheckMap.put(v, 1L);
        }
    }

    /**
     *
     * @param r
     * @param classToCheck
     * @param classToCheckCount
     * @param classMap
     * @param mins
     * @param maxs
     * @param classCounts
     * @param desiredNumberOfValuesInEachClass
     * @param classToFill
     * @return Value for classToFill (may be the same as what is passed in).
     */
    private <T> int checkClassToFillAndPropagation(
            int[] r,
            int classToCheck,
            long classToCheckCount,
            TreeMap<Integer, TreeMap<T, Long>> classMap,
            TreeMap<Integer, T> mins,
            TreeMap<Integer, T> maxs,
            TreeMap<Integer, Long> classCounts,
            long desiredNumberOfValuesInEachClass,
            int classToFill) {
        long classToCheckCountOfMaxValue;
        T classToCheckMaxValue = maxs.get(classToCheck);
        TreeMap<T, Long> classToCheckMap = classMap.get(classToCheck);
        classToCheckCountOfMaxValue = classToCheckMap.get(classToCheckMaxValue);
        if (classToCheckCount - classToCheckCountOfMaxValue < desiredNumberOfValuesInEachClass) {
            r[1] = classToFill;
        } else {
            int nextClassToCheck;
            nextClassToCheck = classToCheck + 1;
            // Push the values up into the next class, adjust the min and max values, checkValueCounts again.
            // Push the values up into the next class
            // --------------------------------------
            // 1. Remove
            classCounts.put(classToCheck, classToCheckCount - classToCheckCountOfMaxValue);
            classToCheckMap.remove(classToCheckMaxValue);
            // 2. Add
            TreeMap<T, Long> nextClassToCheckMap = classMap.get(nextClassToCheck);
            nextClassToCheckMap.put(classToCheckMaxValue, classToCheckCountOfMaxValue);
            // 2.1 Adjust min and max values
            maxs.put(classToCheck, classToCheckMap.lastKey());
//            try {
//            maxDouble.put(classToCheck, classToCheckMap.lastKey());
//            } catch (NoSuchElementException e) {
//                int debug = 1;
//            }
            mins.put(nextClassToCheck, classToCheckMaxValue);
            long nextClassToCheckCount;
            nextClassToCheckCount = classCounts.get(nextClassToCheck);
            if (nextClassToCheckCount == 0) {
                maxs.put(nextClassToCheck, classToCheckMaxValue);
                // There should not be any value bigger in nextClasstoCheck.
            }
            // 2.2 Add to classCounts
            nextClassToCheckCount += classToCheckCountOfMaxValue;
            classCounts.put(nextClassToCheck, nextClassToCheckCount);
            if (classToFill < nextClassToCheck) {
                classToFill = nextClassToCheck;
            }
            // 2.3. Check this class again then check the next class
            classToCheckCount = classCounts.get(classToCheck);
            r[1] = checkClassToFillAndPropagation(r, classToCheck,
                    classToCheckCount, classMap, mins, maxs, classCounts,
                    desiredNumberOfValuesInEachClass, classToFill);
            // nextClassToCheckCount needs to be got again as it may have changed!
            nextClassToCheckCount = classCounts.get(nextClassToCheck);
            r[1] = checkClassToFillAndPropagation(r, nextClassToCheck,
                    nextClassToCheckCount, classMap, mins, maxs, classCounts,
                    desiredNumberOfValuesInEachClass, classToFill);
        }
        return r[1];
    }

    /**
     * @param sum to set sum to.
     */
    public void setSum(BigRational sum) {
        this.sum = sum;
    }

    /**
     * @param nMin to set nMin to.
     */
    public void setNMin(long nMin) {
        this.nMin = nMin;
    }

    /**
     * @param nMax to set nMax to.
     */
    public void setNMax(long nMax) {
        this.nMax = nMax;
    }

    /**
     * @return the nMin
     */
    public long getNMin() {
        return nMin;
    }

    /**
     * @return the nMax
     */
    public long getNMax() {
        return nMax;
    }

}
