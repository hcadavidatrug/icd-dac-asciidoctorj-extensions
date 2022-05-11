from setuptools import setup, find_packages
  
with open('requirements.txt') as f:
    requirements = f.readlines()
  
long_description = 'Transforms a SystemRDL specification into a text-based \
      third-party format defined by a Jinja2 template.'
      
setup(
        name ='sysrdl2jinja',
        version ='0.01',
        author ='Hector Cadavid',
        author_email ='hector.cadavid@gmail.com',
        url ='https://github.com/hcadavid/sysrdl2jinja',
        description ='SystemRDL to Jinja2 template.',
        long_description = long_description,
        long_description_content_type ="text/markdown",
        license ='MIT',
        packages = find_packages(),
        entry_points ={
            'console_scripts': [
                'sysrdl2jinja = sysrdl2jinja.systemrdl2jinjatemplate:main'
            ]
        },
        classifiers =(
            "Programming Language :: Python :: 3",
            "License :: OSI Approved :: MIT License",
            "Operating System :: OS Independent",
        ),
        keywords ='SystemRDL Jinja2',
        install_requires = requirements,
        zip_safe = False
)
