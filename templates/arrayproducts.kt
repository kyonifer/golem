/**
 * THIS FILE IS AUTOGENERATED, DO NOT MODIFY. EDIT THE FILES IN templates/
 * AND RUN ./gradlew :codegen INSTEAD!
 */

/**
 * This file defines inner/outer products between NDArrays.
 */

package koma

import koma.internal.default.utils.sumDoubles
import koma.internal.default.utils.sumFloats
import koma.internal.default.utils.sumLongs
import koma.ndarray.NDArray
import koma.internal.KomaJvmName

private fun dotLong(array1: NDArray<*>, array2: NDArray<*>): Long {
    if (array1.size != array2.size)
        throw IllegalArgumentException("Cannot compute a dot product between arrays of different size")
    return sumLongs(array1.size) { array1.getLong(it)*array2.getLong(it) }
}

private fun dotFloat(array1: NDArray<*>, array2: NDArray<*>): Float {
    if (array1.size != array2.size)
        throw IllegalArgumentException("Cannot compute a dot product between arrays of different size")
    return sumFloats(array1.size) { array1.getFloat(it)*array2.getFloat(it) }.toFloat()
}

private fun dotDouble(array1: NDArray<*>, array2: NDArray<*>): Double {
    if (array1.size != array2.size)
        throw IllegalArgumentException("Cannot compute a dot product between arrays of different size")
    return sumDoubles(array1.size) { array1.getDouble(it)*array2.getDouble(it) }
}

private fun innerLong(array1: NDArray<*>, array2: NDArray<*>, axis1: Int, axis2: Int): NDArray<Long> {
    val shape1 = array1.shape()
    val shape2 = array2.shape()
    if (axis1 < 0 || axis1 >= shape1.size)
        throw IllegalArgumentException("Illegal axis (\$axis1) for a NDArray with \${shape1.size} dimensions")
    if (axis2 < 0 || axis2 >= shape2.size)
        throw IllegalArgumentException("Illegal aixs (\$axis2) for a NDArray with \${shape2.size} dimensions")
    val length = shape1[axis1]
    if (length != shape2[axis2])
        throw IllegalArgumentException("Cannot compute inner product between axes of different length (\$length and \${shape2[axis2]}")
    val newShape = ArrayList<Int>()
    for (i in 0 until shape1.size)
        if (i != axis1)
            newShape.add(shape1[i])
    for (i in 0 until shape2.size)
        if (i != axis2)
            newShape.add(shape2[i])
    if (newShape.size == 0)
        throw IllegalArgumentException("The NDArray produced by inner() must have at least one dimension.  Use dot() for a scalar inner product.")
    val index1 = IntArray(shape1.size)
    val index2 = IntArray(shape2.size)
    return NDArray(*newShape.toIntArray()) { newIndex: IntArray ->
        var j = 0
        for (i in 0 until newIndex.size) {
            if (j == axis1)
                j++
            if (j == axis2+shape1.size)
                j++
            if (j < shape1.size)
                index1[j] = newIndex[i]
            else
                index2[j-shape1.size] = newIndex[i]
            j++
        }
        sumLongs(length) {
            index1[axis1] = it
            index2[axis2] = it
            array1.getLong(array1.nIdxToLinear(index1)) * array2.getLong(array2.nIdxToLinear(index2))
        }
    }
}

private fun innerFloat(array1: NDArray<*>, array2: NDArray<*>, axis1: Int, axis2: Int): NDArray<Float> {
    val shape1 = array1.shape()
    val shape2 = array2.shape()
    if (axis1 < 0 || axis1 >= shape1.size)
        throw IllegalArgumentException("Illegal axis (\$axis1) for a NDArray with \${shape1.size} dimensions")
    if (axis2 < 0 || axis2 >= shape2.size)
        throw IllegalArgumentException("Illegal axis (\$axis2) for a NDArray with \${shape2.size} dimensions")
    val length = shape1[axis1]
    if (length != shape2[axis2])
        throw IllegalArgumentException("Cannot compute inner product between axes of different length (\$length and \${shape2[axis2]}")
    val newShape = ArrayList<Int>()
    for (i in 0 until shape1.size)
        if (i != axis1)
            newShape.add(shape1[i])
    for (i in 0 until shape2.size)
        if (i != axis2)
            newShape.add(shape2[i])
    if (newShape.size == 0)
        throw IllegalArgumentException("The NDArray produced by inner() must have at least one dimension.  Use dot() for a scalar inner product.")
    val index1 = IntArray(shape1.size)
    val index2 = IntArray(shape2.size)
    return NDArray(*newShape.toIntArray()) { newIndex: IntArray ->
        var j = 0
        for (i in 0 until newIndex.size) {
            if (j == axis1)
                j++
            if (j == axis2+shape1.size)
                j++
            if (j < shape1.size)
                index1[j] = newIndex[i]
            else
                index2[j-shape1.size] = newIndex[i]
            j++
        }
        sumFloats(length) {
            index1[axis1] = it
            index2[axis2] = it
            array1.getFloat(array1.nIdxToLinear(index1)) * array2.getFloat(array2.nIdxToLinear(index2))
        }.toFloat()
    }
}

private fun innerDouble(array1: NDArray<*>, array2: NDArray<*>, axis1: Int, axis2: Int): NDArray<Double> {
    val shape1 = array1.shape()
    val shape2 = array2.shape()
    if (axis1 < 0 || axis1 >= shape1.size)
        throw IllegalArgumentException("Illegal axis (\$axis1) for a NDArray with \${shape1.size} dimensions")
    if (axis2 < 0 || axis2 >= shape2.size)
        throw IllegalArgumentException("Illegal axis (\$axis2) for a NDArray with \${shape2.size} dimensions")
    val length = shape1[axis1]
    if (length != shape2[axis2])
        throw IllegalArgumentException("Cannot compute inner product between axes of different length (\$length and \${shape2[axis2]}")
    val newShape = ArrayList<Int>()
    for (i in 0 until shape1.size)
        if (i != axis1)
            newShape.add(shape1[i])
    for (i in 0 until shape2.size)
        if (i != axis2)
            newShape.add(shape2[i])
    if (newShape.size == 0)
        throw IllegalArgumentException("The NDArray produced by inner() must have at least one dimension.  Use dot() for a scalar inner product.")
    val index1 = IntArray(shape1.size)
    val index2 = IntArray(shape2.size)
    return NDArray(*newShape.toIntArray()) { newIndex: IntArray ->
        var j = 0
        for (i in 0 until newIndex.size) {
            if (j == axis1)
                j++
            if (j == axis2+shape1.size)
                j++
            if (j < shape1.size)
                index1[j] = newIndex[i]
            else
                index2[j-shape1.size] = newIndex[i]
            j++
        }
        sumDoubles(length) {
            index1[axis1] = it
            index2[axis2] = it
            array1.getDouble(array1.nIdxToLinear(index1)) * array2.getDouble(array2.nIdxToLinear(index2))
        }
    }
}

private fun outerLong(array1: NDArray<*>, array2: NDArray<*>): NDArray<Long> {
    val shape1 = array1.shape()
    val shape2 = array2.shape()
    val newShape = (shape1+shape2).toIntArray()
    return NDArray(*newShape) { index: IntArray ->
        val index1 = index.sliceArray(0 until shape1.size)
        val index2 = index.sliceArray(shape1.size until index.size)
        array1.getLong(array1.nIdxToLinear(index1)) * array2.getLong(array2.nIdxToLinear(index2))
    }
}

private fun outerFloat(array1: NDArray<*>, array2: NDArray<*>): NDArray<Float> {
    val shape1 = array1.shape()
    val shape2 = array2.shape()
    val newShape = (shape1+shape2).toIntArray()
    return NDArray(*newShape) { index: IntArray ->
        val index1 = index.sliceArray(0 until shape1.size)
        val index2 = index.sliceArray(shape1.size until index.size)
        array1.getFloat(array1.nIdxToLinear(index1)) * array2.getFloat(array2.nIdxToLinear(index2))
    }
}

private fun outerDouble(array1: NDArray<*>, array2: NDArray<*>): NDArray<Double> {
    val shape1 = array1.shape()
    val shape2 = array2.shape()
    val newShape = (shape1+shape2).toIntArray()
    return NDArray(*newShape) { index: IntArray ->
        val index1 = index.sliceArray(0 until shape1.size)
        val index2 = index.sliceArray(shape1.size until index.size)
        array1.getDouble(array1.nIdxToLinear(index1)) * array2.getDouble(array2.nIdxToLinear(index2))
    }
}

$typedProducts