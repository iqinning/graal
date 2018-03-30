/*
 * Copyright (c) 2017, 2018, Oracle and/or its affiliates.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.truffle.llvm.runtime.interop;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.llvm.runtime.LLVMAddress;
import com.oracle.truffle.llvm.runtime.LLVMBoxedPrimitive;
import com.oracle.truffle.llvm.runtime.LLVMContext;
import com.oracle.truffle.llvm.runtime.LLVMFunctionDescriptor;
import com.oracle.truffle.llvm.runtime.LLVMIVarBit;
import com.oracle.truffle.llvm.runtime.LLVMSharedGlobalVariable;
import com.oracle.truffle.llvm.runtime.LLVMTruffleAddress;
import com.oracle.truffle.llvm.runtime.LLVMTruffleObject;
import com.oracle.truffle.llvm.runtime.LLVMVirtualAllocationAddress;
import com.oracle.truffle.llvm.runtime.LLVMVirtualAllocationAddress.LLVMVirtualAllocationAddressTruffleObject;
import com.oracle.truffle.llvm.runtime.global.LLVMGlobal;
import com.oracle.truffle.llvm.runtime.interop.LLVMDataEscapeNodeGen.ManagedEscapeNodeGen;
import com.oracle.truffle.llvm.runtime.types.PointerType;
import com.oracle.truffle.llvm.runtime.types.Type;
import com.oracle.truffle.llvm.runtime.vector.LLVMDoubleVector;
import com.oracle.truffle.llvm.runtime.vector.LLVMFloatVector;
import com.oracle.truffle.llvm.runtime.vector.LLVMI16Vector;
import com.oracle.truffle.llvm.runtime.vector.LLVMI1Vector;
import com.oracle.truffle.llvm.runtime.vector.LLVMI32Vector;
import com.oracle.truffle.llvm.runtime.vector.LLVMI64Vector;
import com.oracle.truffle.llvm.runtime.vector.LLVMI8Vector;

/**
 * Values that escape Sulong and flow to other languages must be primitive or TruffleObject. This
 * node ensures that.
 */
@SuppressWarnings("unused")
public abstract class LLVMDataEscapeNode extends Node {

    private final Type typeForExport;

    public LLVMDataEscapeNode(Type typeForExport) {
        this.typeForExport = typeForExport;
    }

    public abstract Object executeWithTarget(Object escapingValue);

    @Specialization
    protected Object escapingPrimitive(boolean escapingValue) {
        return escapingValue;
    }

    @Specialization
    protected Object escapingPrimitive(byte escapingValue) {
        return escapingValue;
    }

    @Specialization
    protected Object escapingPrimitive(short escapingValue) {
        return escapingValue;
    }

    @Specialization
    protected Object escapingPrimitive(char escapingValue) {
        return escapingValue;
    }

    @Specialization
    protected Object escapingPrimitive(int escapingValue) {
        return escapingValue;
    }

    @Specialization
    protected Object escapingPrimitive(long escapingValue) {
        return escapingValue;
    }

    @Specialization
    protected Object escapingPrimitive(float escapingValue) {
        return escapingValue;
    }

    @Specialization
    protected Object escapingPrimitive(double escapingValue) {
        return escapingValue;
    }

    @Specialization
    protected Object escapingString(String escapingValue) {
        return escapingValue;
    }

    @Specialization
    protected Object escapingString(LLVMBoxedPrimitive escapingValue) {
        return escapingValue.getValue();
    }

    @Specialization
    protected TruffleObject escapingAddress(LLVMAddress escapingValue) {
        if (LLVMAddress.nullPointer().equals(escapingValue)) {
            return new LLVMTruffleAddress(LLVMAddress.fromLong(0), new PointerType(null));
        }
        assert typeForExport != null;
        return new LLVMTruffleAddress(escapingValue, typeForExport);
    }

    @Specialization
    protected TruffleObject escapingFunction(LLVMFunctionDescriptor escapingValue) {
        return escapingValue;
    }

    @Specialization
    protected TruffleObject escapingVector(LLVMI8Vector vector) {
        CompilerDirectives.transferToInterpreter();
        throw new IllegalStateException("Exporting Vectors is not yet supported!");
    }

    @Specialization
    protected TruffleObject escapingVector(LLVMI64Vector vector) {
        CompilerDirectives.transferToInterpreter();
        throw new IllegalStateException("Exporting Vectors is not yet supported!");
    }

    @Specialization
    protected TruffleObject escapingVector(LLVMI32Vector vector) {
        CompilerDirectives.transferToInterpreter();
        throw new IllegalStateException("Exporting Vectors is not yet supported!");
    }

    @Specialization
    protected TruffleObject escapingVector(LLVMI1Vector vector) {
        CompilerDirectives.transferToInterpreter();
        throw new IllegalStateException("Exporting Vectors is not yet supported!");
    }

    @Specialization
    protected TruffleObject escapingVector(LLVMI16Vector vector) {
        CompilerDirectives.transferToInterpreter();
        throw new IllegalStateException("Exporting Vectors is not yet supported!");
    }

    @Specialization
    protected TruffleObject escapingVector(LLVMFloatVector vector) {
        CompilerDirectives.transferToInterpreter();
        throw new IllegalStateException("Exporting Vectors is not yet supported!");
    }

    @Specialization
    protected TruffleObject escapingVector(LLVMDoubleVector vector) {
        CompilerDirectives.transferToInterpreter();
        throw new IllegalStateException("Exporting Vectors is not yet supported!");
    }

    @Specialization
    protected TruffleObject escapingVarbit(LLVMIVarBit vector) {
        CompilerDirectives.transferToInterpreter();
        throw new IllegalStateException("Exporting VarBit is not yet supported!");
    }

    @Specialization
    protected TruffleObject escapingTruffleObject(LLVMTruffleAddress address) {
        return address;
    }

    @Specialization
    TruffleObject escapingTruffleObject(LLVMTruffleObject address,
                    @Cached("create()") ManagedEscapeNode managedEscape) {
        if (address.getOffset() == 0) {
            return managedEscape.execute(address.getObject());
        } else {
            CompilerDirectives.transferToInterpreter();
            throw new IllegalStateException("TruffleObject after pointer arithmetic must not leave Sulong.");
        }
    }

    abstract static class ManagedEscapeNode extends Node {

        abstract TruffleObject execute(TruffleObject object);

        @Specialization
        TruffleObject doForeign(LLVMTypedForeignObject object) {
            return object.getForeign();
        }

        @Fallback
        TruffleObject doOther(TruffleObject object) {
            return object;
        }

        public static ManagedEscapeNode create() {
            return ManagedEscapeNodeGen.create();
        }
    }

    @Specialization
    protected TruffleObject escapingJavaByteArray(LLVMVirtualAllocationAddress address) {
        return new LLVMVirtualAllocationAddressTruffleObject(address.copy());
    }

    @Specialization
    protected Object escapingTruffleObject(LLVMGlobal escapingValue) {
        return new LLVMSharedGlobalVariable(escapingValue);
    }

    @Specialization(guards = "escapingValue == null")
    protected Object escapingNull(Object escapingValue) {
        return new LLVMTruffleAddress(LLVMAddress.nullPointer(), new PointerType(null));
    }

    @TruffleBoundary
    public static Object slowConvert(Object value, Type type) {
        if (value instanceof LLVMBoxedPrimitive) {
            return ((LLVMBoxedPrimitive) value).getValue();
        } else if (value instanceof LLVMAddress && LLVMAddress.nullPointer().equals(value)) {
            return new LLVMTruffleAddress(LLVMAddress.nullPointer(), new PointerType(null));
        } else if (value instanceof LLVMAddress) {
            return new LLVMTruffleAddress((LLVMAddress) value, type);
        } else if (value instanceof LLVMTruffleObject && ((LLVMTruffleObject) value).getOffset() == 0) {
            return ((LLVMTruffleObject) value).getObject();
        } else if (value instanceof LLVMTruffleObject) {
            throw new IllegalStateException("TruffleObject after pointer arithmetic must not leave Sulong.");
        } else if (value instanceof LLVMVirtualAllocationAddress) {
            return new LLVMVirtualAllocationAddressTruffleObject(((LLVMVirtualAllocationAddress) value).copy());
        } else if (value instanceof LLVMGlobal) {
            return new LLVMSharedGlobalVariable((LLVMGlobal) value);
        } else if (value == null) {
            return new LLVMTruffleAddress(LLVMAddress.nullPointer(), new PointerType(null));
        } else {
            return value;
        }
    }
}
