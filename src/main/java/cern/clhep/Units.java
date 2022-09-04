/**
 * Copyright (C) 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
 * is hereby granted without fee, provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear in supporting documentation.
 * CERN makes no representations about the suitability of this software for any purpose.
 * It is provided "as is" without expressed or implied warranty.
 */
package cern.clhep;

/**
 * High Energy Physics coherent system of Units based on Geant4 (a simulation toolkit for HEP).
 */
@SuppressWarnings("unused")
public final class Units {

    public static final double millimeter = 1.0;

    public static final double millimeter2 = millimeter * millimeter;
    public static final double mm2 = millimeter2;
    public static final double millimeter3 = millimeter * millimeter * millimeter;
    public static final double mm3 = millimeter3;
    public static final double centimeter = 10. * millimeter;
    public static final double centimeter2 = centimeter * centimeter;
    public static final double cm2 = centimeter2;
    public static final double centimeter3 = centimeter * centimeter * centimeter;
    public static final double cm3 = centimeter3;
    public static final double cm = centimeter;
    public static final double meter = 1000. * millimeter;
    public static final double meter2 = meter * meter;
    public static final double barn = 1.e-28 * meter2;
    public static final double millibarn = 1.e-3 * barn;
    public static final double microbarn = 1.e-6 * barn;
    public static final double nanobarn = 1.e-9 * barn;
    public static final double picobarn = 1.e-12 * barn;
    public static final double m2 = meter2;
    public static final double meter3 = meter * meter * meter;
    public static final double m3 = meter3;
    public static final double kilometer = 1000. * meter;
    public static final double kilometer2 = kilometer * kilometer;
    public static final double km2 = kilometer2;
    public static final double kilometer3 = kilometer * kilometer * kilometer;
    public static final double km3 = kilometer3;
    public static final double km = kilometer;
    public static final double micrometer = 1.e-6 * meter;
    public static final double nanometer = 1.e-9 * meter;
    public static final double angstrom = 1.e-10 * meter;
    public static final double fermi = 1.e-15 * meter;
    public static final double m = meter;

    public static final double mm = millimeter;

    public static final double radian = 1.;

    public static final double milliradian = 1.e-3 * radian;
    public static final double mrad = milliradian;
    public static final double degree = (Math.PI / 180.0) * radian;
    public static final double deg = degree;
    public static final double steradian = 1.;

    public static final double rad = radian;
    public static final double sr = steradian;

    public static final double nanosecond = 1.;

    public static final double second = 1.e+9 * nanosecond;

    public static final double millisecond = 1.e-3 * second;
    public static final double ms = millisecond;
    public static final double microsecond = 1.e-6 * second;
    public static final double picosecond = 1.e-12 * second;
    public static final double hertz = 1. / second;
    public static final double kilohertz = 1.e+3 * hertz;
    public static final double megahertz = 1.e+6 * hertz;
    public static final double s = second;

    public static final double becquerel = 1. / second;
    public static final double curie = 3.7e+10 * becquerel;

    public static final double ns = nanosecond;

    public static final double eplus = 1.;

    public static final double e_SI = 1.60217733e-19;
    public static final double coulomb = eplus / e_SI;

    public static final double ampere = coulomb / second;
    public static final double milliampere = 1.e-3 * ampere;
    public static final double microampere = 1.e-6 * ampere;
    public static final double nanoampere = 1.e-9 * ampere;

    public static final double megaelectronvolt = 1.;

    public static final double electronvolt = 1.e-6 * megaelectronvolt;
    public static final double joule = electronvolt / e_SI;

    public static final double kilogram = joule * second * second / (meter * meter);
    public static final double gram = 1.e-3 * kilogram;
    public static final double milligram = 1.e-3 * gram;
    public static final double mg = milligram;
    public static final double g = gram;

    public static final double kg = kilogram;

    public static final double gray = joule / kilogram;

    public static final double watt = joule / second;

    public static final double newton = joule / meter;

    public static final double hep_pascal = newton / m2;
    public static final double pascal = hep_pascal;

    public static final double bar = 100000 * pascal;

    public static final double atmosphere = 101325 * pascal;

    public static final double eV = electronvolt;
    public static final double kiloelectronvolt = 1.e-3 * megaelectronvolt;

    public static final double keV = kiloelectronvolt;

    public static final double gigaelectronvolt = 1.e+3 * megaelectronvolt;

    public static final double GeV = gigaelectronvolt;
    public static final double teraelectronvolt = 1.e+6 * megaelectronvolt;
    public static final double TeV = teraelectronvolt;
    public static final double petaelectronvolt = 1.e+9 * megaelectronvolt;
    public static final double PeV = petaelectronvolt;
    public static final double MeV = megaelectronvolt;
    public static final double megavolt = megaelectronvolt / eplus;

    public static final double kilovolt = 1.e-3 * megavolt;

    public static final double volt = 1.e-6 * megavolt;

    public static final double ohm = volt / ampere;

    public static final double farad = coulomb / volt;
    public static final double millifarad = 1.e-3 * farad;
    public static final double microfarad = 1.e-6 * farad;

    public static final double nanofarad = 1.e-9 * farad;

    public static final double picofarad = 1.e-12 * farad;

    public static final double weber = volt * second;

    public static final double henry = weber / ampere;

    public static final double tesla = volt * second / meter2;
    public static final double gauss = 1.e-4 * tesla;
    public static final double kilogauss = 1.e-1 * tesla;

    public static final double kelvin = 1.;

    public static final double mole = 1.;

    public static final double candela = 1.;

    public static final double lumen = candela * steradian;

    public static final double lux = lumen / meter2;

    public static final double perCent = 0.01;

    public static final double perThousand = 0.001;

    public static final double perMillion = 0.000001;

    private Units() {
        throw new AssertionError("Do not instantiate.");
    }
}
