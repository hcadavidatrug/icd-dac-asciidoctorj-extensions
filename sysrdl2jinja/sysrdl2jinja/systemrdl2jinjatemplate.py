import sys
import os
from systemrdl import RDLCompiler, RDLCompileError, RDLWalker
from jinja2 import Template

class FailedQualityGate (Exception):
    pass

def check_quality_gates (_rdl_root):
    if "bigendian" not in _rdl_root.top.inst.properties and "littleendian" not in _rdl_root.top.inst.properties:
        raise FailedQualityGate('Undefined endianness for the register map ' + _rdl_root.top.inst_name)

def parse_rdl_file (_rdl_file):
    # Create an instance of the compiler
    rdlc = RDLCompiler()

    rdlc.compile_file(_rdl_file)

    # Elaborate the design
    root = rdlc.elaborate()

    _map_name = root.top.inst_name
    _registers = []


    # iterate over registries definition
    for register_def in root.top.inst.children:
        register = dict()
        register["name"] = register_def.inst_name.upper()
        register["offset"] = hex(register_def.addr_offset)
        fields = []
        register["fields"] = fields

        for field_def in register_def.children:
            field = dict()
            field["id"] = field_def.inst_name.upper()
            field["low"] = field_def.low
            field["high"] = field_def.high
            field["lsb"] = field_def.lsb
            field["msb"] = field_def.msb
            field["width"] = field_def.width
            if "name" in field_def.properties:
                field["name"] = field_def.properties["name"].upper()
            else:
                field["name"] = "undefined"
            if "hw" in field_def.properties:
                field["hwrights"] = str(field_def.properties["hw"]).replace("AccessType.", "")
            else:
                field["hwrights"] = "undefined"
            if "sw" in field_def.properties:
                field["swrights"] = str(field_def.properties["sw"]). replace("AccessType.", "")
            else:
                field["swrights"] = "undefined"
            if "reset" in field_def.properties:
                field["reset"] = field_def.properties["reset"]
            else:
                field["reset"] = "undefined"
            fields.append(field)

        _registers.append(register)

    return _registers, _map_name, root


def main():

    if len(sys.argv) < 4:
        print("SystemRDL to Jinja template converter", file=sys.stderr)
        print("Syntax: ", sys.argv[0], " [rdl_file] [jinja_template_file] [output_file]", file=sys.stderr)
        exit(1)


    # Collect input files from the command line arguments
    rdl_file = sys.argv[1]
    template_file = sys.argv[2]
    output_file = sys.argv[3]

    try:
        registers, map_name, root = parse_rdl_file(rdl_file)
        check_quality_gates(root)
        with open(template_file) as file_:
            template = Template(file_.read())
        with open(output_file, 'w') as output_:
            output_.write(template.render(registers=registers, map_name=map_name.upper()))
        exit(0)
    except RDLCompileError as exc:
        # error details are sent to stderr by the parser, next line add further details
        print("File: ", rdl_file, file=sys.stderr)
        exit(1)
    except FailedQualityGate as exc:
        print("File: ", rdl_file, exc, file=sys.stderr)
