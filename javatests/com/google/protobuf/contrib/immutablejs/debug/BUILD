load("//javatests/com/google/protobuf/contrib/immutablejs/build_def:proto_test.bzl", "proto_jsunit_test")

licenses(["notice"])

proto_jsunit_test(
    name = "dump_test",
    srcs = ["dump_test.js"],
    deps = [
        "//java/com/google/protobuf/contrib/immutablejs:debug",
        "//java/com/google/protobuf/contrib/immutablejs:runtime",
        "//javascript/closure:base",
        "//javatests/com/google/protobuf/contrib/immutablejs/protos:accessors_immutable_js_proto",
        "//third_party/javascript/closure/math:long",
        "//third_party/javascript/closure/testing:asserts",
        "//third_party/javascript/closure/testing:testsuite",
    ],
)
