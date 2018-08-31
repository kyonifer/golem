@file:koma.internal.JvmName("NDArray")
@file:koma.internal.JvmMultifileClass

/**
 * THIS FILE IS AUTOGENERATED, DO NOT MODIFY. EDIT THE FILES IN templates/
 * AND RUN ./gradlew :codegen INSTEAD!
 */

package koma.extensions

import koma.internal.default.generated.ndarray.DefaultGenericNDArray
import koma.internal.default.utils.checkIndices
import koma.internal.default.utils.linearToNIdx
import koma.matrix.doubleFactory
import koma.ndarray.NDArray
import koma.ndarray.NumericalNDArrayFactory
import koma.internal.default.utils.nIdxToLinear
import koma.pow
import koma.matrix.Matrix

@koma.internal.JvmName("toMatrixFloat")
fun NDArray<Float>.toMatrix(): Matrix<Float> {
    if (this is Matrix)
        return this
    val dims = this.shape()
    return when (dims.size) {
        1 -> { Matrix.floatFactory.zeros(dims[0], 1).fill { row, _ -> this[row] } }
        2 -> { Matrix.floatFactory.zeros(dims[0], dims[1]).fill { row, col -> this[row, col] } }
        else -> error("Cannot convert NDArray with ${dims.size} dimensions to matrix (must be 1 or 2)")
    }
}

@koma.internal.JvmName("fillFloat")
inline fun  NDArray<Float>.fill(f: (idx: IntArray) -> Float) = apply {
    for ((nd, linear) in this.iterateIndices())
        this.setFloat(linear, f(nd))
}

@koma.internal.JvmName("fillFloatBoth")
inline fun  NDArray<Float>.fillBoth(f: (nd: IntArray, linear: Int) -> Float) = apply {
    for ((nd, linear) in this.iterateIndices())
        this.setFloat(linear, f(nd, linear))
}

@koma.internal.JvmName("fillFloatLinear")
inline fun  NDArray<Float>.fillLinear(f: (idx: Int) -> Float) = apply {
    for (idx in 0 until size)
        this.setFloat(idx, f(idx))
}

@koma.internal.JvmName("createFloat")
inline fun  NumericalNDArrayFactory<Float>.create(vararg lengths: Int, filler: (idx: IntArray) -> Float)
    = NDArray.floatFactory.zeros(*lengths).fill(filler)


/**
 * Returns a new NDArray with the given shape, populated with the data in this array.
 *
 * @param dims Desired dimensions of the output array.
 *
 * @returns A copy of the elements in this array, shaped to the given number of rows and columns,
 *          such that `this.toList() == this.reshape(*dims).toList()`
 *
 * @throws IllegalArgumentException when the product of all of the given `dims` does not equal [size]
 */
@koma.internal.JvmName("reshapeFloat")
fun  NDArray<Float>.reshape(vararg dims: Int): NDArray<Float> {
    if (dims.reduce { a, b -> a * b } != size)
        throw IllegalArgumentException("$size items cannot be reshaped to ${dims.toList()}")
    var idx = 0
    return NDArray.floatFactory.zeros(*dims).fill { _ -> getFloat(idx++) }
}


/**
 * Takes each element in a NDArray, passes them through f, and puts the output of f into an
 * output NDArray.
 *
 * @param f A function that takes in an element and returns an element
 *
 * @return the new NDArray after each element is mapped through f
 */
@koma.internal.JvmName("mapFloat")
inline fun  NDArray<Float>.map(f: (Float) -> Float)
    = NDArray.floatFactory.zeros(*shape().toIntArray()).fillLinear { f(this.getFloat(it)) }


/**
 * Takes each element in a NDArray, passes them through f, and puts the output of f into an
 * output NDArray. Index given to f is a linear index, depending on the underlying storage
 * major dimension.
 *
 * @param f A function that takes in an element and returns an element. Function also takes
 *      in the linear index of the element's location.
 *
 * @return the new NDArray after each element is mapped through f
 */
@koma.internal.JvmName("mapIndexedFloat")
inline fun  NDArray<Float>.mapIndexed(f: (idx: Int, ele: Float) -> Float)
    = NDArray.floatFactory.zeros(*shape().toIntArray()).fillLinear { f(it, this.getFloat(it)) }


/**
 * Takes each element in a NDArray and passes them through f.
 *
 * @param f A function that takes in an element
 *
 */
@koma.internal.JvmName("forEachFloat")
inline fun  NDArray<Float>.forEach(f: (ele: Float) -> Unit) {
    // TODO: Change this back to iteration once there are non-boxing iterators
    for (idx in 0 until size)
        f(getFloat(idx))
}
/**
 * Takes each element in a NDArray and passes them through f. Index given to f is a linear
 * index, depending on the underlying storage major dimension.
 *
 * @param f A function that takes in an element. Function also takes
 *      in the linear index of the element's location.
 *
 */
@koma.internal.JvmName("forEachIndexedFloat")
inline fun  NDArray<Float>.forEachIndexed(f: (idx: Int, ele: Float) -> Unit) {
    // TODO: Change this back to iteration once there are non-boxing iterators
    for (idx in 0 until size)
        f(idx, getFloat(idx))
}

/**
 * Takes each element in a NDArray, passes them through f, and puts the output of f into an
 * output NDArray. Index given to f is the full ND index of the element.
 *
 * @param f A function that takes in an element and returns an element. Function also takes
 *      in the ND index of the element's location.
 *
 * @return the new NDArray after each element is mapped through f
 */
@koma.internal.JvmName("mapIndexedNFloat")
inline fun  NDArray<Float>.mapIndexedN(f: (idx: IntArray, ele: Float) -> Float): NDArray<Float>
    = NDArray.floatFactory.zeros(*shape().toIntArray()).fillBoth { nd, linear -> f(nd, getFloat(linear)) }


/**
 * Takes each element in a NDArray and passes them through f. Index given to f is the full
 * ND index of the element.
 *
 * @param f A function that takes in an element. Function also takes
 *      in the ND index of the element's location.
 *
 */
@koma.internal.JvmName("forEachIndexedNFloat")
inline fun  NDArray<Float>.forEachIndexedN(f: (idx: IntArray, ele: Float) -> Unit) {
    for ((nd, linear) in iterateIndices())
        f(nd, getFloat(linear))
}

/**
 * Converts this NDArray into a one-dimensional FloatArray in row-major order.
 */
fun  NDArray<Float>.toFloatArray() = FloatArray(size) { getFloat(it) }

@koma.internal.JvmName("getRangesFloat")
operator fun  NDArray<Float>.get(vararg indices: IntRange): NDArray<Float> {
    checkIndices(indices.map { it.last }.toIntArray())
    return DefaultGenericNDArray<Float>(shape = *indices
            .map { it.last - it.first + 1 }
            .toIntArray()) { newIdxs ->
        val offsets = indices.map { it.first }
        val oldIdxs = newIdxs.zip(offsets).map { it.first + it.second }
        this.getGeneric(*oldIdxs.toIntArray())
    }
}

@koma.internal.JvmName("setFloat")
operator fun  NDArray<Float>.set(vararg indices: Int, value: NDArray<Float>) {
    val shape = shape()
    val lastIndex = indices.mapIndexed { i, range -> range + value.shape()[i] }
    val outOfBounds = lastIndex.withIndex().any { it.value > shape()[it.index] }
    if (outOfBounds)
        throw IllegalArgumentException("NDArray with shape ${shape()} cannot be " +
                "set at ${indices.toList()} by a ${value.shape()} array " +
                "(out of bounds)")

    val offset = indices.map { it }.toIntArray()
    value.forEachIndexedN { idx, ele ->
        val newIdx = offset.zip(idx).map { it.first + it.second }.toIntArray()
        this.setGeneric(indices=*newIdx, v=ele)
    }
}


operator fun  NDArray<Float>.get(vararg indices: Int) = getFloat(*indices)
operator fun  NDArray<Float>.set(vararg indices: Int, value: Float) = setFloat(indices=*indices, v=value)


@koma.internal.JvmName("divFloat")
operator fun NDArray<Float>.div(other: Float) = map { (it/other).toFloat() }
@koma.internal.JvmName("timesArrFloat")
operator fun NDArray<Float>.times(other: NDArray<Float>) = mapIndexedN { idx, ele -> (ele*other.get(*idx)).toFloat() }
@koma.internal.JvmName("timesFloat")
operator fun NDArray<Float>.times(other: Float) = map { (it * other).toFloat() }
@koma.internal.JvmName("unaryMinusFloat")
operator fun NDArray<Float>.unaryMinus() = map { (-it).toFloat() }
@koma.internal.JvmName("minusFloat")
operator fun NDArray<Float>.minus(other: Float) = map { (it - other).toFloat() }
@koma.internal.JvmName("minusArrFloat")
operator fun NDArray<Float>.minus(other: NDArray<Float>) = mapIndexedN { idx, ele -> (ele - other.get(*idx)).toFloat() }
@koma.internal.JvmName("plusFloat")
operator fun NDArray<Float>.plus(other: Float) = map { (it + other).toFloat() }
@koma.internal.JvmName("plusArrFloat")
operator fun NDArray<Float>.plus(other: NDArray<Float>) = mapIndexedN { idx, ele -> (ele + other.get(*idx)).toFloat() }
@koma.internal.JvmName("powFloat")
infix fun NDArray<Float>.pow(exponent: Int) = map { pow(it.toDouble(), exponent).toFloat() }

