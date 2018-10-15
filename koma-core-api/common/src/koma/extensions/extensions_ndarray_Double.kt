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
import koma.ndarray.NDArray
import koma.ndarray.NumericalNDArrayFactory
import koma.internal.default.utils.nIdxToLinear
import koma.pow
import koma.matrix.Matrix

@koma.internal.JvmName("toMatrixDouble")
fun NDArray<Double>.toMatrix(): Matrix<Double> {
    if (this is Matrix)
        return this
    val dims = this.shape()
    return when (dims.size) {
        1 -> { Matrix.doubleFactory.zeros(dims[0], 1).fill { row, _ -> this[row] } }
        2 -> { Matrix.doubleFactory.zeros(dims[0], dims[1]).fill { row, col -> this[row, col] } }
        else -> error("Cannot convert NDArray with ${dims.size} dimensions to matrix (must be 1 or 2)")
    }
}

@koma.internal.JvmName("fillDouble")
inline fun  NDArray<Double>.fill(f: (idx: IntArray) -> Double) = apply {
    for ((nd, linear) in this.iterateIndices())
        this.setDouble(linear, f(nd))
}

@koma.internal.JvmName("fillDoubleBoth")
inline fun  NDArray<Double>.fillBoth(f: (nd: IntArray, linear: Int) -> Double) = apply {
    for ((nd, linear) in this.iterateIndices())
        this.setDouble(linear, f(nd, linear))
}

@koma.internal.JvmName("fillDoubleLinear")
inline fun  NDArray<Double>.fillLinear(f: (idx: Int) -> Double) = apply {
    for (idx in 0 until size)
        this.setDouble(idx, f(idx))
}

@koma.internal.JvmName("createDouble")
inline fun  NumericalNDArrayFactory<Double>.create(vararg lengths: Int, filler: (idx: IntArray) -> Double)
    = NDArray.doubleFactory.zeros(*lengths).fill(filler)


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
@koma.internal.JvmName("reshapeDouble")
fun  NDArray<Double>.reshape(vararg dims: Int): NDArray<Double> {
    if (dims.reduce { a, b -> a * b } != size)
        throw IllegalArgumentException("$size items cannot be reshaped to ${dims.toList()}")
    var idx = 0
    return NDArray.doubleFactory.zeros(*dims).fill { _ -> getDouble(idx++) }
}


/**
 * Takes each element in a NDArray, passes them through f, and puts the output of f into an
 * output NDArray.
 *
 * @param f A function that takes in an element and returns an element
 *
 * @return the new NDArray after each element is mapped through f
 */
@koma.internal.JvmName("mapDouble")
inline fun <reified R> NDArray<Double>.map(crossinline f: (Double) -> R)
    = NDArray.createLinear(*shape().toIntArray(), filler={ f(this.getDouble(it)) } )

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
@koma.internal.JvmName("mapIndexedDouble")
inline fun  NDArray<Double>.mapIndexed(f: (idx: Int, ele: Double) -> Double)
    = NDArray.doubleFactory.zeros(*shape().toIntArray()).fillLinear { f(it, this.getDouble(it)) }


/**
 * Takes each element in a NDArray and passes them through f.
 *
 * @param f A function that takes in an element
 *
 */
@koma.internal.JvmName("forEachDouble")
inline fun  NDArray<Double>.forEach(f: (ele: Double) -> Unit) {
    // TODO: Change this back to iteration once there are non-boxing iterators
    for (idx in 0 until size)
        f(getDouble(idx))
}
/**
 * Takes each element in a NDArray and passes them through f. Index given to f is a linear
 * index, depending on the underlying storage major dimension.
 *
 * @param f A function that takes in an element. Function also takes
 *      in the linear index of the element's location.
 *
 */
@koma.internal.JvmName("forEachIndexedDouble")
inline fun  NDArray<Double>.forEachIndexed(f: (idx: Int, ele: Double) -> Unit) {
    // TODO: Change this back to iteration once there are non-boxing iterators
    for (idx in 0 until size)
        f(idx, getDouble(idx))
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
@koma.internal.JvmName("mapIndexedNDouble")
inline fun  NDArray<Double>.mapIndexedN(f: (idx: IntArray, ele: Double) -> Double): NDArray<Double>
    = NDArray.doubleFactory.zeros(*shape().toIntArray()).fillBoth { nd, linear -> f(nd, getDouble(linear)) }


/**
 * Takes each element in a NDArray and passes them through f. Index given to f is the full
 * ND index of the element.
 *
 * @param f A function that takes in an element. Function also takes
 *      in the ND index of the element's location.
 *
 */
@koma.internal.JvmName("forEachIndexedNDouble")
inline fun  NDArray<Double>.forEachIndexedN(f: (idx: IntArray, ele: Double) -> Unit) {
    for ((nd, linear) in iterateIndices())
        f(nd, getDouble(linear))
}

/**
 * Converts this NDArray into a one-dimensional DoubleArray in row-major order.
 */
fun  NDArray<Double>.toDoubleArray() = DoubleArray(size) { getDouble(it) }

@koma.internal.JvmName("getRangesDouble")
operator fun  NDArray<Double>.get(vararg indices: IntRange): NDArray<Double> {
    checkIndices(indices.map { it.last }.toIntArray())
    return DefaultGenericNDArray<Double>(shape = *indices
            .map { it.last - it.first + 1 }
            .toIntArray()) { newIdxs ->
        val offsets = indices.map { it.first }
        val oldIdxs = newIdxs.zip(offsets).map { it.first + it.second }
        this.getGeneric(*oldIdxs.toIntArray())
    }
}

@koma.internal.JvmName("setDouble")
operator fun  NDArray<Double>.set(vararg indices: Int, value: NDArray<Double>) {
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


operator fun  NDArray<Double>.get(vararg indices: Int) = getDouble(*indices)
operator fun  NDArray<Double>.set(vararg indices: Int, value: Double) = setDouble(indices=*indices, v=value)


@koma.internal.JvmName("divDouble")
operator fun NDArray<Double>.div(other: Double) = map { (it/other).toDouble() }
@koma.internal.JvmName("timesArrDouble")
operator fun NDArray<Double>.times(other: NDArray<Double>) = mapIndexedN { idx, ele -> (ele*other.get(*idx)).toDouble() }
@koma.internal.JvmName("timesDouble")
operator fun NDArray<Double>.times(other: Double) = map { (it * other).toDouble() }
@koma.internal.JvmName("unaryMinusDouble")
operator fun NDArray<Double>.unaryMinus() = map { (-it).toDouble() }
@koma.internal.JvmName("minusDouble")
operator fun NDArray<Double>.minus(other: Double) = map { (it - other).toDouble() }
@koma.internal.JvmName("minusArrDouble")
operator fun NDArray<Double>.minus(other: NDArray<Double>) = mapIndexedN { idx, ele -> (ele - other.get(*idx)).toDouble() }
@koma.internal.JvmName("plusDouble")
operator fun NDArray<Double>.plus(other: Double) = map { (it + other).toDouble() }
@koma.internal.JvmName("plusArrDouble")
operator fun NDArray<Double>.plus(other: NDArray<Double>) = mapIndexedN { idx, ele -> (ele + other.get(*idx)).toDouble() }
@koma.internal.JvmName("powDouble")
infix fun NDArray<Double>.pow(exponent: Int) = map { pow(it.toDouble(), exponent).toDouble() }

