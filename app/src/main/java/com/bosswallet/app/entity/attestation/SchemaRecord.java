package com.bosswallet.app.entity.attestation;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;

public class SchemaRecord extends DynamicStruct
{
    public byte[] uid;
    public Address resolver;
    public boolean revocable;
    public String schema;

    public SchemaRecord(byte[] uid, Address resolver, boolean revocable, String schema)
    {
        super(
                new Bytes32(uid),
                new Address(resolver.getValue()),
                new Bool(revocable),
                new Utf8String(schema));
        this.uid = uid;
        this.resolver = resolver;
        this.revocable = revocable;
        this.schema = schema;
    }

    public SchemaRecord(Bytes32 uid, Address resolver, Bool revocable, Utf8String schema)
    {
        super(uid, resolver, revocable, schema);
        this.uid = uid.getValue();
        this.resolver = resolver;
        this.revocable = revocable.getValue();
        this.schema = schema.getValue();
    }
}
